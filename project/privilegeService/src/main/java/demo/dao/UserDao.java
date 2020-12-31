package demo.dao;

import com.example.model.VoObject;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.example.util.encript.AES;
import com.example.util.encript.SHA256;
import com.github.pagehelper.PageInfo;
import demo.model.vo.UserVo;
import demo.repository.*;
import demo.model.bo.Privilege;
import demo.model.bo.Role;
import demo.model.bo.User;
import demo.model.bo.UserRole;
import demo.model.po.RolePo;
import demo.model.po.UserPo;
import demo.model.po.UserProxyPo;
import demo.model.po.UserRolePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);


    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    PrivilegeRepository privilegeRepository;
    @Autowired
    RolePrivilegeRepository rolePrivilegeRepository;

    @Autowired
    UserProxyRepository userProxyRepository;
    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;
    @Value("${privilegeservice.user.expiretime}")
    private long timeout;
    @Autowired
    RoleDao roleDao;

    public Mono<Boolean> checkUserDid(Long userid, Long departid) {
        return userRepository.findById(userid).defaultIfEmpty(new UserPo()).map(userPo -> {
            if(userPo.getId()==null||userPo.getDepartId()!=departid){
                return false;
            }else{
                return true;
            }
        });
    }

    /**
     * 取消用户角色
     * @param userid 用户id
     * @param roleid 角色id
     * @return ReturnObject<VoObject>
     * @author Xianwei Wang
     * */
    public Mono<ReturnObject<VoObject>> revokeRole(Long userid, Long roleid){
        Mono<UserPo> userPoMono=userRepository.findById(userid).defaultIfEmpty(new UserPo());
        Mono<RolePo> rolePoMono=roleRepository.findById(roleid).defaultIfEmpty(new RolePo());
        Mono<Integer> deleteResult=userRoleRepository.deleteUserRolePoByUserIdAndAndRoleId(userid,roleid).defaultIfEmpty(-1);
        return Mono.zip(userPoMono,rolePoMono,deleteResult).map(tuple->{
            if(tuple.getT1().getId()==null||tuple.getT2().getId()==null){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else if(tuple.getT3()==-1){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else{
                return new ReturnObject<>();
            }
        });
    }

    public Mono<Boolean> checkRoleDid(Long roleid, Long departId) {
        return roleRepository.findById(roleid).defaultIfEmpty(new RolePo()).map(rolePo -> {
            return rolePo.getId() != null && rolePo.getDepartId().equals(departId);
        });
    }


    public Mono<ReturnObject<VoObject>> assignRole(Long createid, Long userid, Long roleid) {
        Mono<UserPo> userPoMono=userRepository.findById(userid).defaultIfEmpty(new UserPo());
        Mono<UserPo> createMono=userRepository.findById(createid).defaultIfEmpty(new UserPo());
        Mono<RolePo> rolePoMono=roleRepository.findById(roleid).defaultIfEmpty(new RolePo());

        Mono<UserRolePo> userRolePoMono=userRoleRepository.findByUserIdAndRoleId(userid,roleid).defaultIfEmpty(new UserRolePo());

        logger.info("enter it");
        return Mono.zip(userPoMono,createMono,rolePoMono,userRolePoMono).flatMap(tuple->{
            if(tuple.getT1().getId()==null||tuple.getT2().getId()==null||tuple.getT3().getId()==null){
                logger.info("enter one");

                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }else if(tuple.getT4().getId()!=null){
                logger.info("enter two");

                logger.warn("assignRole: 该用户已拥有该角色 userid=" + userid + "roleid=" + roleid);
                return Mono.just(new ReturnObject<>(ResponseCode.USER_ROLE_REGISTERED));
            }else{
                logger.info("enter three");
                UserRolePo userRolePo = new UserRolePo();
                userRolePo.setUserId(userid);
                userRolePo.setRoleId(roleid);
                userRolePo.setCreatorId(createid);
                userRolePo.setGmtCreate(LocalDateTime.now());
                UserRole userRole = new UserRole(userRolePo, new User(tuple.getT1()), new Role(tuple.getT3()), new User(tuple.getT2()));
                userRolePo.setSignature(userRole.getCacuSignature());

//                userRoleRepository.save(userRolePo);
                return userRoleRepository.save(userRolePo)
                        .map(userRolePoRet->{
                            userRole.setId(userRolePoRet.getId());
                            return userRole;
                        }).map(ReturnObject::new);
//                return Mono.just(new ReturnObject<>(userRole));
            }
        });
    }

    public Mono<ReturnObject<List>> getUserRoles(Long id) {
        Mono<User> userMono=userRepository.findById(id).map(User::new);
//        Flux<UserRole> userRoleFlux=Flux.empty();
        return userRoleRepository.findAllByUserId(id).flatMap(
                userRolePo -> {
                    Mono<User> creatorMono=userRepository.findById(userRolePo.getCreatorId()).map(User::new).log();
                    Mono<Role> roleMono=roleRepository.findById(userRolePo.getRoleId()).map(Role::new).log();
                    return Mono.zip(creatorMono,roleMono,userMono).map(tuple->{
                        if(tuple.getT2()!=null&&tuple.getT1()!=null){
                            UserRole userRole = new UserRole(userRolePo, tuple.getT3(), tuple.getT2(), tuple.getT1());
                            //校验签名
//                                    if (userRole.authetic()){
//                                        userRoleFlux.concatWithValues(userRole);
//                                        logger.info("getRoleIdByUserId: userId = " + userRolePo.getUserId() + " roleId = " + userRolePo.getRoleId());
//                                    } else {
//                                        logger.error("getUserRoles: Wrong Signature(auth_user_role): id =" + userRolePo.getId());
//                                    }
                            return userRole;
                        }
                        return new UserRole(userRolePo, tuple.getT3(), tuple.getT2(), tuple.getT1());
                    });
                }
        ).collect(Collectors.toList()).map(ReturnObject::new);
    }

    public Mono<ReturnObject<List>> findPrivsByUserId(Long id, Long did) {
        return userRepository.findById(id).flatMap(userPo -> {
            if (userPo == null) {//判断是否是由于用户不存在造成的
                logger.error("findPrivsByUserId: 数据库不存在该用户 userid=" + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            Long departId = userPo.getDepartId();
            if(departId != did) {
                logger.error("findPrivsByUserId: 店铺id不匹配 userid=" + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }

            return userRoleRepository.findAllByUserId(id)
                    .flatMap(userRolePo -> rolePrivilegeRepository.findAllByRoleId(userRolePo.getRoleId()))
                    .flatMap(rolePrivilegePo -> privilegeRepository.findById(rolePrivilegePo.getPrivilegeId()))
                    .map(Privilege::new).collect(Collectors.toList()).map(ReturnObject::new);
        });
    }

    public Mono<UserPo> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<PageInfo<UserPo>> findAllUsers(String userNameAES, String mobileAES, Integer page, Integer pagesize) {
//        return userRepository.findAllByUserNameAndMobile(userNameAES,mobileAES)
        return userRepository.findAll()
                .collect(Collectors.toList()).map(PageInfo::new);
    }

    public Mono<ReturnObject<User>> getUserByName(String userName) {
//        try{
        System.out.println("userdao-getUserByName");
        Mono<UserPo> findResult = userRepository.findByUserName(userName);
        System.out.println("userdao-findByUserName");
        if ( findResult== null) {
            System.out.println("not-found-user-byName");
            return Mono.just(new ReturnObject<>());
        } else {
            return findResult.map(users->{
                User user = new User(users);
                if (!user.authetic()) {
                    StringBuilder message = new StringBuilder().append("getUserByName: ").append("id= ")
                            .append(user.getId()).append(" username=").append(user.getUserName());
                    logger.error(message.toString());
                    return new ReturnObject<>(ResponseCode.RESOURCE_FALSIFY);
                } else {
                    return new ReturnObject<>(user);
                }
            });
        }
//        }catch (DataAccessException e){
//            StringBuilder message = new StringBuilder().append("getUserByName: ").append(e.getMessage());
//            logger.error(message.toString());
//            return Mono.just(new ReturnObject<>()).map(ReturnObject::new);
//        }
    }

    public Mono<Integer> loadUserPriv(Long id, String jwt) {

        String key = "u_" + id;
        String aKey = "up_" + id;
        System.out.println("userdao-loadUserPriv");
        return getProxyIdsByUserId(id).map(proxyIds->{
            System.out.println("userdao-getProxyIdsByUserId-overnext");
            List<String> proxyUserKey = new ArrayList<>(proxyIds.size());
            for (Long proxyId : proxyIds) {
                if (!redisTemplate.hasKey("u_" + proxyId)) {
                    logger.debug("loadUserPriv: loading proxy user. proxId = " + proxyId);
                    System.out.println("userdao-loadSingleUserPriv-begin-1");
                    loadSingleUserPriv(proxyId);
                    System.out.println("userdao-loadSingleUserPriv-over-1");
                }
                proxyUserKey.add("u_" + proxyId);
            }
            if (!redisTemplate.hasKey(key)) {
                logger.debug("loadUserPriv: loading user. id = " + id);
                System.out.println("userdao-loadSingleUserPriv-begin-2");
                loadSingleUserPriv(id);
                System.out.println("userdao-loadSingleUserPriv-over-2");
            }
            redisTemplate.opsForSet().unionAndStore(key, proxyUserKey, aKey);
            redisTemplate.opsForSet().add(aKey, jwt);
            long randTimeout = Common.addRandomTime(timeout);
            redisTemplate.expire(aKey, randTimeout, TimeUnit.SECONDS);
            return 0;
        });
    }

    public Mono<Boolean> setLoginIPAndPosition(Long userId, String IPAddr, LocalDateTime date) {
        System.out.println("userdao-setLoginIPAndPosition");
        System.out.println("id:"+userId);
        System.out.println("ip:"+IPAddr);
        System.out.println("time:"+LocalDateTime.now());
        return userRepository.findById(userId).map(userPo->{
            System.out.println("userdao-userRepository.findById");
            userPo.setLastLoginIp(IPAddr);
            userPo.setLastLoginTime(date);
            userRepository.save(userPo);
            return true;
        });
    }

    private Mono<List<Long>> getProxyIdsByUserId(Long id) {
        System.out.println("userdao-getProxyIdsByUserId");
        return userProxyRepository.findAllByUserAId(id).collect(Collectors.toList()).map(userProxyPos->{
            System.out.println("userdao-userProxyRepository.findAllByUserAId");
            List<Long> retIds = new ArrayList<>(userProxyPos.size());
            LocalDateTime now = LocalDateTime.now();
            for (UserProxyPo po : userProxyPos) {
                StringBuilder signature = Common.concatString("-", po.getUserAId().toString(),
                        po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
                String newSignature = SHA256.getSHA256(signature.toString());
                UserProxyPo newPo = null;

                if (newSignature.equals(po.getSignature())) {
                    if (now.isBefore(po.getEndDate()) && now.isAfter(po.getBeginDate())) {
                        //在有效期内
                        retIds.add(po.getUserBId());
                        logger.debug("getProxyIdsByUserId: userAId = " + po.getUserAId() + " userBId = " + po.getUserBId());
                    } else {
                        //代理过期了，但标志位依然是有效
                        newPo = newPo == null ? new UserProxyPo() : newPo;
                        newPo.setValid((byte) 0);
                        signature = Common.concatString("-", po.getUserAId().toString(),
                                po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), newPo.getValid().toString());
                        newSignature = SHA256.getSHA256(signature.toString());
                        newPo.setSignature(newSignature);
                    }
                } else {
                    logger.error("getProxyIdsByUserId: Wrong Signature(auth_user_proxy): id =" + po.getId());
                }

                if (null != newPo) {
                    logger.debug("getProxyIdsByUserId: writing back.. po =" + newPo);
                    po.setValid((byte) 0);
                    po.setSignature(newSignature);
                    userProxyRepository.save(newPo);
                }
            }
            return retIds;
        });
    }

    private Mono<Integer> loadSingleUserPriv(Long id) {
        System.out.println("userDao-loadSingleUserPriv");
        return getRoleIdByUserId(id).map(roleIds->{
            String key = "u_" + id;
            Set<String> roleKeys = new HashSet<>(roleIds.size());
            for (Long roleId : roleIds) {
                String roleKey = "r_" + roleId;
                roleKeys.add(roleKey);
                if (!redisTemplate.hasKey(roleKey)) {
                    roleDao.loadRolePriv(roleId);
                }
                redisTemplate.opsForSet().unionAndStore(roleKeys, key);
            }
            redisTemplate.opsForSet().add(key, 0);
            long randTimeout = Common.addRandomTime(timeout);
            redisTemplate.expire(key, randTimeout, TimeUnit.SECONDS);
            return 0;
        });
    }

    private Mono<List<Long>> getRoleIdByUserId(Long id) {
        System.out.println("userdao-getRoleIdByUserId");
        return userRoleRepository.findAllByUserId(id).collect(Collectors.toList()).map(userRolePoList->{
            System.out.println("userdao-userRoleRepository.findByUserId");
            logger.debug("getRoleIdByUserId: userId = " + id + "roleNum = " + userRolePoList.size());
            List<Long> retIds = new ArrayList<>(userRolePoList.size());
            for (UserRolePo po : userRolePoList) {
                StringBuilder signature = Common.concatString("-",
                        po.getUserId().toString(), po.getRoleId().toString(), po.getCreatorId().toString());
                String newSignature = SHA256.getSHA256(signature.toString());


                if (newSignature.equals(po.getSignature())) {
                    retIds.add(po.getRoleId());
                    logger.debug("getRoleIdByUserId: userId = " + po.getUserId() + " roleId = " + po.getRoleId());
                } else {
                    logger.error("getRoleIdByUserId: 签名错误(auth_role_privilege): id =" + po.getId());
                }
            }
            return retIds;
        });
    }

    public Mono<ReturnObject> changeUserState(Long id, User.State state){
        System.out.println("userdao-changeuserstate");
        return createUserStateModPo(id, state).flatMap(po->{
            System.out.println("userdao-createUserStateModPo-onnext");
            if (po == null) {
                logger.info("用户不存在或已被删除：id = " + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            else{
//                try {
                logger.info("用户 id = " + id + " 的状态修改为 " + state.getDescription());
                System.out.println("po:"+po.getState()+"  "+po.getEmail()+"  "+po.getMobile()+"  "+po.getName());
                return userRepository.save(po).map(res->{
                    System.out.println("res:"+res.getState()+"  "+res.getEmail()+"  "+res.getMobile()+"  "+res.getName());
                    return new ReturnObject<>();
                });
//                }catch (DataAccessException e) {
//                    // 数据库错误
//                    logger.error("数据库错误：" + e.getMessage());
//                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                            String.format("发生了严重的数据库错误：%s", e.getMessage()));
//                } catch (Exception e) {
//                    // 属未知错误
//                    logger.error("严重错误：" + e.getMessage());
//                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                            String.format("发生了严重的未知错误：%s", e.getMessage())) ;
//                }

            }

        });
    }

    private Mono<UserPo> createUserStateModPo(Long id, User.State state) {
        System.out.println("userDao-createUserStateModPo");
        return userRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                System.out.println("userdao-notfound-user");
                return Mono.just(null);
            } else if (resOptional.get().getState() != null && User.State.getTypeByCode(resOptional.get().getState().intValue()) == User.State.DELETE) {
                System.out.println("userdao-user-deleted");
                return Mono.just(null);
            } else {
                // 构造 User 对象以计算签名
                User user = new User(resOptional.get());
                user.setState(state);

                System.out.println("user:"+user.getState()+"  "+user.getEmail()+"  "+user.getMobile()+"  "+user.getName());
                // 构造一个全为 null 的 vo 因为其他字段都不用更新
                UserVo vo = new UserVo();
                UserPo userPo = user.createUpdatePo(vo,resOptional.get());
                userPo.setMobileVerified(resOptional.get().getMobileVerified());
                userPo.setEmailVerified(resOptional.get().getEmailVerified());
                userPo.setEmail(AES.encrypt(user.getEmail(),User.AESPASS));
                userPo.setMobile(AES.encrypt(user.getMobile(),User.AESPASS));
                userPo.setName(AES.encrypt(user.getName(),User.AESPASS));
                System.out.println("userpo:"+userPo.getState()+"  "+userPo.getEmail()+"  "+userPo.getMobile()+"  "+userPo.getName());
                return Mono.just(userPo);
            }
        });
    }

    public Mono<ReturnObject> modifyUserByVo(Long id, UserVo userVo) {
        // 查询密码等资料以计算新签名
        return userRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional->{
            if (!resOptional.isPresent()){
                logger.info("用户不存在或已被删除：id = " + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }else if(resOptional.get().getState() != null && User.State.getTypeByCode(resOptional.get().getState().intValue()) == User.State.DELETE){
                logger.info("用户不存在或已被删除：id = " + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }else{
                // 构造 User 对象以计算签名
                User user = new User(resOptional.get());
                UserPo po = user.createUpdatePo(userVo,resOptional.get());
                po = reSetUserPo(resOptional.get(),po);

                // 将更改的联系方式 (如发生变化) 的已验证字段改为 false
                if (userVo.getEmail() != null && !userVo.getEmail().equals(user.getEmail())) {
                    po.setEmailVerified((byte) 0);
                }
                if (userVo.getMobile() != null && !userVo.getMobile().equals(user.getMobile())) {
                    po.setMobileVerified((byte) 0);
                }
                // 更新并检查更新有否成功
                return userRepository.save(po).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional2->{
                    if (!resOptional2.isPresent()){
                        logger.info("用户不存在或已被删除：id = " + id);
                        return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                    }else {
                        logger.info("用户 id = " + id + " 的资料已更新");
                        return Mono.just(new ReturnObject<>());
                    }
                });

            }
        });
    }

    public UserPo reSetUserPo(UserPo old,UserPo newPo){
        newPo.setEmailVerified(old.getEmailVerified());
        newPo.setMobileVerified(old.getMobileVerified());
        if(newPo.getName()==null){
            newPo.setName(old.getName());
        }
        if(newPo.getAvatar()==null){
            newPo.setAvatar(old.getAvatar());
        }
        if(newPo.getEmail()==null){
            newPo.setEmail(old.getEmail());
        }
        if (newPo.getMobile()==null){
            newPo.setMobile(old.getMobile());
        }
        return newPo;
    }

}
