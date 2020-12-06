package demo.dao;

import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.encript.SHA256;
import demo.model.bo.User;
import demo.model.po.*;
import demo.repository.UserProxyRepository;
import demo.repository.UserRepository;
import demo.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author chei1
 */
@Repository
public class UserDao {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserProxyRepository userProxyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    public ReturnObject addUser(NewUserPo po) {
        return new ReturnObject(
                userRepository.save(
                UserPo.builder().email(AES.encrypt(po.getEmail(), User.AESPASS))
                        .mobile(AES.encrypt(po.getMobile(), User.AESPASS))
                        .userName(po.getUserName()).avatar(po.getAvatar())
                        .departId(po.getDepartId()).openId(po.getOpenId())
                        .gmtCreate(LocalDateTime.now()).build()
        ).log());
    }

//        ReturnObject returnObject = null;
//        UserPo userPo = UserPo.builder().build();
//        userPo.setEmail(AES.encrypt(po.getEmail(), User.AESPASS));
//        userPo.setMobile(AES.encrypt(po.getMobile(), User.AESPASS));
//        userPo.setUserName(po.getUserName());
//        userPo.setAvatar(po.getAvatar());
//        userPo.setDepartId(po.getDepartId());
//        userPo.setOpenId(po.getOpenId());
//        userPo.setGmtCreate(LocalDateTime.now());
//        try{
//           returnObject = new ReturnObject<>(userRepository.save(userPo));
//        }
//        catch (DataAccessException e)
//        {
//            if (Objects.requireNonNull(e.getMessage()).contains("auth_user.user_name_uindex")) {
//                //若有重复名则修改失败
//                returnObject = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("用户名重复：" + userPo.getName()));
//            } else {
//                returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//            }
//        }
//
//        catch (Exception e) {
//            // 其他Exception错误
//            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
//        }
//        return returnObject;


    /**
     * 清除缓存中的与role关联的user
     *
     * @param id 角色id
     */
    public void clearUserByRoleId(Long id){
        userRoleRepository.findAllByRoleId(id).map(it->{
            clearUserPrivCache(it.getUserId());
            return null;
        });

//        UserRolePoExample example = new UserRolePoExample();
//        UserRolePoExample.Criteria criteria = example.createCriteria();
//        criteria.andRoleIdEqualTo(id);
//        Long uid;
//        for(UserRolePo e:userrolePos){
//            uid = e.getUserId();
//            clearUserPrivCache(uid);
//        }
    }
    /**
     * 使用用户id，清空该用户和被代理对象的redis缓存
     * @param userid 用户id
     * @author Xianwei Wang
     */
    private void clearUserPrivCache(Long userid){
        String key = "u_" + userid;
        redisTemplate.delete(key);

//        UserProxyPoExample example = new UserProxyPoExample();
//        UserProxyPoExample.Criteria criteria = example.createCriteria();
//        criteria.andUserBIdEqualTo(userid);
//        List<UserProxyPo> userProxyPoList = userProxyPoMapper.selectByExample(example);

        List<UserProxyPo> userProxyPoList =userProxyRepository.findAllByUserBId(userid).collectList().block();


        LocalDateTime now = LocalDateTime.now();

        for (UserProxyPo po:
                userProxyPoList) {
            StringBuilder signature = Common.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            UserProxyPo newPo = null;

            if (newSignature.equals(po.getSignature())) {
                if (now.isBefore(po.getEndDate()) && now.isAfter(po.getBeginDate())) {
                    //在有效期内
                    String proxyKey = "up_" + po.getUserAId();
                    redisTemplate.delete(proxyKey);
                    //logger.debug("clearUserPrivCache: userAId = " + po.getUserAId() + " userBId = " + po.getUserBId());
                } else {
                    //代理过期了，但标志位依然是有效
                    newPo = newPo == null ? UserProxyPo.builder().build() : newPo;
                    newPo.setValid((byte) 0);
                    signature = Common.concatString("-", po.getUserAId().toString(),
                            po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), newPo.getValid().toString());
                    newSignature = SHA256.getSHA256(signature.toString());
                    newPo.setSignature(newSignature);
                }
            } else {
                //logger.error("clearUserPrivCache: Wrong Signature(auth_user_proxy): id =" + po.getId());
            }

            if (null != newPo) {
                //logger.debug("clearUserPrivCache: writing back.. po =" + newPo);
                //userProxyPoMapper.updateByPrimaryKeySelective(newPo);
                userProxyRepository.save(newPo);
            }

        }
    }
}
