package demo.service;

import com.example.model.VoObject;
import com.example.util.Common;
import com.example.util.RandomCaptcha;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.example.util.encript.AES;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import demo.Repository.UserRepository;
import demo.Repository.UserRoleRepository;
import demo.dao.PrivilegeDao;
import demo.dao.UserDao;
import demo.model.bo.User;
import demo.model.po.UserPo;
import demo.model.vo.ModifyPwdVo;
import demo.model.vo.PrivilegeVo;
import demo.model.vo.ResetPwdVo;
import demo.model.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserDao userDao;
    @Autowired
    PrivilegeDao privilegeDao;

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    RedisTemplate redisTemplate;
//    @Autowired
//    private RedisTemplate<String, Serializable> redisTemplate;



    /**
     * 取消用户角色
     * @param userid 用户id
     * @param roleid 角色id
     * @param did departid
     * @return ReturnObject<VoObject>
     * @author Xianwei Wang
     * */
    @Transactional
    public Mono<ReturnObject<VoObject>> revokeRole(Long userid, Long roleid, Long did){
        return Mono.zip(userDao.checkUserDid(userid,did),userDao.checkRoleDid(roleid, did)).flatMap(
            tuple-> {
                if (tuple.getT1() && tuple.getT2() || did == Long.valueOf(0)) {
                    return userDao.revokeRole(userid, roleid);
                } else {
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                }
            });
    }

    public Mono<ReturnObject<VoObject>> assignRole(Long createid, Long userid, Long roleid, Long did) {

        return Mono.zip(userDao.checkUserDid(userid,did),userDao.checkRoleDid(roleid, did)).flatMap(
                tuple-> {
                    if (tuple.getT1() && tuple.getT2() || did == Long.valueOf(0)) {
                        return userDao.assignRole(createid, userid, roleid);
                    } else {
                        return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                    }
                });

    }

    public Mono<ReturnObject<List>> getSelfUserRoles(Long id) {
        return userDao.getUserRoles(id);
    }

    public Mono<ReturnObject<List>> getUserRoles(Long id, Long did) {
        return userDao.checkUserDid(id, did).flatMap(checkResult->{
            if(checkResult||did == Long.valueOf(0)){
                return userDao.getUserRoles(id);
            }else{
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
        });
    }

    public Mono<ReturnObject> changePriv(Long id, PrivilegeVo vo) {
        return privilegeDao.changePriv(id, vo);
    }

    public Mono<ReturnObject<List>> findPrivsByUserId(Long id, Long did) {
        return userDao.findPrivsByUserId(id,did);
    }

    public Mono<ReturnObject<VoObject>> findUserById(Long userId) {

        return userDao.findUserById(userId).map(userPo -> {
            if(userPo != null) {
                logger.debug("findUserById : " + userPo);
                return new ReturnObject<>(new User(userPo));
            } else {
                logger.debug("findUserById: Not Found");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        });

    }

    public Mono<ReturnObject<PageInfo<VoObject>>> findAllUsers(String userName, String mobile, Integer page, Integer pagesize) {
        String userNameAES = userName.isBlank() ? "" : AES.encrypt(userName, User.AESPASS);
        String mobileAES = mobile.isBlank() ? "" : AES.encrypt(mobile, User.AESPASS);

        PageHelper.startPage(page, pagesize);
        Mono<PageInfo<UserPo>> userPos = userDao.findAllUsers(userName, mobileAES, page, pagesize);
        Mono<List<VoObject>> users=userPos.map(pageInfo->pageInfo.getList().stream().map(User::new)
                .filter(User::authetic).collect(Collectors.toList()));
        System.out.println(users.block());
        return Mono.zip(userPos,users).map(tuple-> {
            PageInfo<VoObject> returnObject = new PageInfo<>(tuple.getT2());
            returnObject.setPages(tuple.getT1().getPages());
            returnObject.setPageNum(tuple.getT1().getPageNum());
            returnObject.setPageSize(tuple.getT1().getPageSize());
            returnObject.setTotal(tuple.getT1().getTotal());
            return new ReturnObject<>(returnObject);
        });
    }

    public Mono<ReturnObject<PageInfo<VoObject>>> findAllPrivs(Integer page, Integer pageSize) {
        return privilegeDao.findAllPrivs(page, pageSize);
    }
    /**
     * huiyu
     */

    @Transactional
    public Mono<ReturnObject> modifyUserInfo(Long id, UserVo userVo) {
        // 查询密码等资料以计算新签名
        return userRepository.findById(id).flatMap(it->{
           if(it == null || (it.getState() != null
                   && User.State.getTypeByCode(it.getState().intValue()) == User.State.DELETE)){
               logger.info("用户不存在或已被删除：id = " + id);
               return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
           }else{
               // 构造 User 对象以计算签名
               User user = new User(it);
               UserPo po = user.createUpdatePo(userVo,it);
               // 将更改的联系方式 (如发生变化) 的已验证字段改为 false
               if (userVo.getEmail() != null && !userVo.getEmail().equals(user.getEmail())) {
                   po.setEmailVerified((byte) 0);
               }
               if (userVo.getMobile() != null && !userVo.getMobile().equals(user.getMobile())) {
                   po.setMobileVerified((byte) 0);
               }

               /**
                * 此处save需作异常处理
                */
               return userRepository.save(po).flatMap(userPo -> {
                   logger.info(userPo.toString());
                   // 检查更新有否成功
                   if (userPo==null) {
                       logger.info("用户不存在或已被删除：id = " + id);
                       return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                   } else {
                       logger.info("用户 id = " + id + " 的资料已更新");
                       return Mono.just(new ReturnObject<>());
                   }
               });
//               // 更新数据库
//               ReturnObject<Object> retObj;
//               Mono<UserPo> ret;
//               try {
//                   ret=userRepository.save(po);
//               } catch (DataAccessException e) {
//                   // 如果发生 Exception，判断是邮箱还是啥重复错误
//                   if (Objects.requireNonNull(e.getMessage()).contains("auth_user.auth_user_mobile_uindex")) {
//                       logger.info("电话重复：" + userVo.getMobile());
//                       retObj = new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
//                   } else if (e.getMessage().contains("auth_user.auth_user_email_uindex")) {
//                       logger.info("邮箱重复：" + userVo.getEmail());
//                       retObj = new ReturnObject<>(ResponseCode.EMAIL_REGISTERED);
//                   } else {
//                       // 其他情况属未知错误
//                       logger.error("数据库错误：" + e.getMessage());
//                       retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                               String.format("发生了严重的数据库错误：%s", e.getMessage()));
//                   }
//                   return retObj;
//               } catch (Exception e) {
//                   // 其他 Exception 即属未知错误
//                   logger.error("严重错误：" + e.getMessage());
//                   return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                           String.format("发生了严重的未知错误：%s", e.getMessage()));
//               }
//               // 检查更新有否成功
//               if (ret.block()!=null) {
//                   logger.info("用户不存在或已被删除：id = " + id);
//                   retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//               } else {
//                   logger.info("用户 id = " + id + " 的资料已更新");
//                   retObj = new ReturnObject<>();
//               }
//               return retObj;
           }
        });

    }

    @Transactional
    public Mono<ReturnObject> resetPassword(ResetPwdVo vo, String ip) {
        return Mono.just(redisTemplate.hasKey("ip_"+ip)).flatMap(flag->{
            if(flag){
                return Mono.just(new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN));
            }else{
                /**
                 * 未配置redis，暂时屏蔽
                 */
                redisTemplate.opsForValue().set("ip_"+ip,ip);
                redisTemplate.expire("ip_" + ip, 60*1000, TimeUnit.MILLISECONDS);
                //验证邮箱、手机号
                return userRepository.findByMobile(AES.encrypt(vo.getMobile(),User.AESPASS)).flatMap(userPo -> {
                    if(!userPo.getEmail().equals(AES.encrypt(vo.getEmail(), User.AESPASS))){
                        return Mono.just(new ReturnObject<>(ResponseCode.EMAIL_WRONG));
                    }else{
                        //随机生成验证码
                        String captcha = RandomCaptcha.getRandomString(6);
                        while(redisTemplate.hasKey(captcha)) {
                            captcha = RandomCaptcha.getRandomString(6);
                        }
                        String id = userPo.getId().toString();
                        String key = "cp_" + captcha;
                        //key:验证码,value:id存入redis
                        redisTemplate.opsForValue().set(key,id);
                        //五分钟后过期
                        redisTemplate.expire("cp_" + captcha, 5*60*1000, TimeUnit.MILLISECONDS);
                        return Mono.just(new ReturnObject<>(captcha));
                    }
                }).defaultIfEmpty(new ReturnObject<>(ResponseCode.MOBILE_WRONG));
            }
        });






//        //发送邮件(请在配置文件application.properties填写密钥)
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setSubject("【oomall】密码重置通知");
//        msg.setSentDate(new Date());
//        msg.setText("您的验证码是：" + captcha + "，5分钟内有效。");
//        msg.setFrom("925882085@qq.com");
//        msg.setTo(vo.getEmail());
//        try {
//            mailSender.send(msg);
//        } catch (MailException e) {
//            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
//        }

    }

    @Transactional
    public Mono<ReturnObject>modifyPassword(ModifyPwdVo modifyPwdVo) {
        return Mono.just(redisTemplate.hasKey("cp_" + modifyPwdVo.getCaptcha())).flatMap(flag -> {
            if (!flag) {
                return Mono.just(new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT));
            } else {
                String id = redisTemplate.opsForValue().get("cp_" + modifyPwdVo.getCaptcha()).toString();
                return userRepository.findById(Long.parseLong(id)).flatMap(userPo -> {
                    if (AES.decrypt(userPo.getPassword(), User.AESPASS).equals(modifyPwdVo.getNewPassword())) {
                        return Mono.just(new ReturnObject<>(ResponseCode.PASSWORD_SAME));
                    }
                    userPo.setPassword(AES.encrypt(modifyPwdVo.getNewPassword(), User.AESPASS));
                    return userRepository.save(userPo).flatMap(it -> Mono.just(new ReturnObject<>(ResponseCode.OK)));
                });

            }
        });
    }

}
