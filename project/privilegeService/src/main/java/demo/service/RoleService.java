package demo.service;

import com.example.model.VoObject;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import demo.Repository.*;
import demo.dao.RoleDao;
import demo.model.bo.Role;
import demo.model.bo.RolePrivilege;
import demo.model.po.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.example.util.encript.SHA256;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RoleService {

    @Autowired
    RoleDao roleDao;

    @Autowired
    RolePrivilegeRepository rolePrivilegeRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PrivilegeRepository privilegeRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    UserProxyRepository userProxyRepository;
    @Autowired
    RedisTemplate redisTemplate;

//    @Autowired
//    private RedisTemplate<String, Serializable> redisTemplate;


    public Mono<ReturnObject<PageInfo<VoObject>>> selectAllRoles(Long departId, Integer page, Integer pageSize) {
        return roleDao.selectAllRole(departId, page, pageSize);
    }

    public Mono<ReturnObject<VoObject>> insertRole(Role role) {
        return roleDao.insertRole(role).map(rolePo -> {
            if (rolePo != null) {
                role.setId(rolePo.getId());
                return new ReturnObject<>(role);
            } else {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        });
    }

    public Mono<ReturnObject> findRolePrivs(Long id) {
        return roleRepository.findById(id).flatMap(rolePo -> {
            if (rolePo == null) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            return getPrivIdsByRoleId(id).flatMap(
                    pid -> rolePrivilegeRepository.findByRoleIdAndPrivilegeId(id, pid).flatMap(
                            po -> {
                                if (po != null) {
                                    return Mono.zip(userRepository.findById(po.getCreatorId()), privilegeRepository.findById(pid)).flatMap(
                                            tuple -> {
                                                RolePrivilege e = new RolePrivilege();
                                                e.setCreator(tuple.getT1());
                                                e.setId(pid);
                                                e.setPrivilege(tuple.getT2());
                                                e.setRole(rolePo);
                                                e.setGmtModified(po.getGmtCreate().toString());
                                                return Mono.just(e);
                                            }
                                    );
                                }
                                return null;
                            })).collectList().flatMap(list -> Mono.just(new ReturnObject(list)));

        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    /**
     * 由Role Id 获得 Privilege Id 列表
     */
    private Flux<Long> getPrivIdsByRoleId(Long id) {
        return rolePrivilegeRepository.findAllByRoleId(id).map(po -> {
            StringBuilder signature = Common.concatString("-", po.getRoleId().toString(),
                    po.getPrivilegeId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            if (newSignature.equals(po.getSignature())) {
                log.debug("getPrivIdsBByRoleId: roleId = " + po.getRoleId() + " privId = " + po.getPrivilegeId());
                return po.getPrivilegeId();
            } else {
                log.info("getPrivIdsBByRoleId: Wrong Signature(auth_role_privilege): id =" + po.getId());
                return null;
            }
        });
    }

    @Transactional
    public Mono<ReturnObject> delRolePriv(Long id){
        return rolePrivilegeRepository.findById(id).flatMap(po->{
            if(po==null){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }else {
                return rolePrivilegeRepository.deleteRolePrivilegePoById(id).flatMap(it->{
                    if(it==1){
                        Long roleid = po.getRoleId();
                        String key = "r_" + roleid;
                        //清除缓存被删除的的角色,重新load
                        if(redisTemplate.hasKey(key)){
                            redisTemplate.delete(key);
                        }
                        return Mono.just(new ReturnObject<>());
                    }else{
                        return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                    }
                });
            }
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST)).flatMap(ret->{
            if(ret.getCode()==ResponseCode.OK) {
                clearuserByroleId(id);
            }
            return Mono.just(ret);
        });
    }

    @Transactional

    public void clearuserByroleId(Long id){
        userRoleRepository.findByRoleId(id).map(po->{
            clearUserPrivCache(po.getUserId());
            return null;
        });
    }

    /**
     * 使用用户id，清空该用户和被代理对象的redis缓存
     * @param userid 用户id
     * @author Xianwei Wang
     */
    private void clearUserPrivCache(Long userid){
        String key = "u_" + userid;
        redisTemplate.delete(key);
        LocalDateTime now = LocalDateTime.now();
        userProxyRepository.findAllByUserBId(userid).map(po->{
            StringBuilder signature = Common.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            UserProxyPo newPo = null;

            if (newSignature.equals(po.getSignature())) {
                if (now.isBefore(po.getEndDate()) && now.isAfter(po.getBeginDate())) {
                    //在有效期内
                    String proxyKey = "up_" + po.getUserAId();
                    redisTemplate.delete(proxyKey);
                    log.debug("clearUserPrivCache: userAId = " + po.getUserAId() + " userBId = " + po.getUserBId());
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
                log.error("clearUserPrivCache: Wrong Signature(auth_user_proxy): id =" + po.getId());
            }

            if (null != newPo) {
                log.debug("clearUserPrivCache: writing back.. po =" + newPo);
                userProxyRepository.save(newPo);
            }
            return null;
        });
    }

    @Transactional
    public Mono<ReturnObject> addRolePriv(Long roleId, Long privId, Long userId){
        return Mono.zip(userRepository.findById(userId),privilegeRepository.findById(privId),roleRepository.findById(roleId)).flatMap(
                tuple->{
                    log.info("三个po都存在");
                    if(tuple.getT1()==null||tuple.getT2()==null||tuple.getT3()==null){
                        return Mono.just(new ReturnObject<VoObject>(ResponseCode.RESOURCE_ID_NOTEXIST));
                    }else{
                        return rolePrivilegeRepository.findByRoleIdAndPrivilegeId(roleId,privId).flatMap(po1->{
                            if(po1!=null){
                                return Mono.just(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("角色权限已存在")));
                            }else{
                                return insertRolePriv(tuple.getT1(),tuple.getT2(),tuple.getT3());
                            }

                        }).switchIfEmpty(insertRolePriv(tuple.getT1(),tuple.getT2(),tuple.getT3())).flatMap(ret->{
                            if(ret.getCode()==ResponseCode.OK) {
                                clearuserByroleId(roleId);
                            }
                            return Mono.just(ret);
                        });
                    }
                }
        );
    }

    public Mono<ReturnObject<?>> insertRolePriv(UserPo userPo,PrivilegePo privilegePo,RolePo rolePo){
        RolePrivilegePo roleprivilegepo = new RolePrivilegePo();
        LocalDateTime localDateTime=LocalDateTime.now();
        roleprivilegepo.setRoleId(rolePo.getId());
        roleprivilegepo.setPrivilegeId(privilegePo.getId());
        roleprivilegepo.setCreatorId(userPo.getId());
        roleprivilegepo.setGmtCreate(localDateTime);
        StringBuilder signature = Common.concatString("-", roleprivilegepo.getRoleId().toString(),
                roleprivilegepo.getPrivilegeId().toString(), roleprivilegepo.getCreatorId().toString(), localDateTime.toString());
        String newSignature = SHA256.getSHA256(signature.toString());
        roleprivilegepo.setSignature(newSignature);
        //return rolePrivilegeRepository.findByRoleIdAndPrivilegeId(rolePo.getId(),privilegePo.getId())
            return rolePrivilegeRepository.save(roleprivilegepo).map(po2->{
                if (po2 == null) {
                    //插入失败
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                } else {
                    //插入成功
                    //清除角色权限
                    String key = "r_" + rolePo.getId();
                    if(redisTemplate.hasKey(key)){
                        redisTemplate.delete(key);
                    }
                    //组装返回的bo
                    RolePrivilege rolePrivilege = new RolePrivilege();
                    rolePrivilege.setId(roleprivilegepo.getId());
                    rolePrivilege.setCreator(userPo);
                    rolePrivilege.setRole(rolePo);
                    rolePrivilege.setPrivilege(privilegePo);
                    rolePrivilege.setGmtModified(localDateTime.toString());

                    return new ReturnObject<VoObject>(rolePrivilege);

                }
            }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            //此处为插入失败，但错误码不匹配

        /**
         * 此处需异常处理
         */
//        }catch (DataAccessException e){
//            // 数据库错误
//            return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage())));
//        }catch (Exception e) {
//            // 其他Exception错误
//            return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage())));
//        }
    }

}
