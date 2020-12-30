package demo.dao;

import com.example.model.VoObject;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.example.util.encript.SHA256;
import com.github.pagehelper.PageInfo;
import demo.controller.PrivilegeController;
import demo.model.po.UserProxyPo;
import demo.model.po.UserRolePo;
import demo.repository.RolePrivilegeRepository;
import demo.repository.RoleRepository;
import demo.repository.UserProxyRepository;
import demo.repository.UserRoleRepository;
import demo.model.bo.Role;
import demo.model.po.RolePo;
import demo.model.po.RolePrivilegePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class RoleDao {
    private static final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    RolePrivilegeRepository rolePrivilegeRepository;

    @Resource
    RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserProxyRepository userProxyRepository;

    @Value("${privilegeservice.role.expiretime}")
    private long timeout;
    public Mono<ReturnObject<PageInfo<VoObject>>> selectAllRole(Long departId, Integer page, Integer pageSize) {
        return roleRepository.findAllByDepartId(departId).map(Role::new)
                .collect(Collectors.toList()).map(list->{
                    List<VoObject> ret = new ArrayList<>(list.size());
                    for(Role role:list){
                        ret.add(role);
                    }
                    return ret;
                }).map(PageInfo::of)
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject<VoObject>> insertRole(Role role) {

        return roleRepository.findByName(role.getName()).defaultIfEmpty(new RolePo())
                .flatMap(rolePo -> {
                    if(rolePo.getId()!=null){
                        return Mono.just(new ReturnObject<>(ResponseCode.ROLE_REGISTERED));
                    }else{
                        return roleRepository.save(role.gotRolePo()).map(savePo -> {
                            if (savePo != null) {
                                role.setId(savePo.getId());
                                return new ReturnObject<>(role);
                            } else {
                                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                            }
                        });
                    }
                });

    }

    public void loadRolePriv(Long id) {
        this.getPrivIdsByRoleId(id).map(privIds->{
            String key = "r_" + id;
            for (Long pId : privIds) {
                redisTemplate.opsForSet().add(key, pId);
            }
            redisTemplate.opsForSet().add(key,0);
            long randTimeout = Common.addRandomTime(this.timeout);
            redisTemplate.expire(key, randTimeout, TimeUnit.SECONDS);
            return null;
        });
    }

    private Mono<List<Long>> getPrivIdsByRoleId(Long id) {
        return rolePrivilegeRepository.findAllByRoleId(id).collect(Collectors.toList()).map(rolePrivilegePos->{
            List<Long> retIds = new ArrayList<>(rolePrivilegePos.size());
            for (RolePrivilegePo po : rolePrivilegePos) {
                StringBuilder signature = Common.concatString("-", po.getRoleId().toString(),
                        po.getPrivilegeId().toString(), po.getCreatorId().toString());
                String newSignature = SHA256.getSHA256(signature.toString());

                if (newSignature.equals(po.getSignature())) {
                    retIds.add(po.getPrivilegeId());
                    logger.debug("getPrivIdsBByRoleId: roleId = " + po.getRoleId() + " privId = " + po.getPrivilegeId());
                } else {
                    logger.info("getPrivIdsBByRoleId: Wrong Signature(auth_role_privilege): id =" + po.getId());
                }
            }
            return retIds;
        });
    }

    public Mono<ReturnObject> deleteRole(Long did, Long id){
//        try {
        System.out.println("roledao-deleteRole");
        return roleRepository.findByIdAndDepartId(id,did).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional->{
            if (!resOptional.isPresent()){
                System.out.println("role isn't exist");
                //删除角色表
                logger.debug("deleteRole: id not exist = " + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("角色id不存在：" + id))) ;
            }else {
                System.out.println("role exist,id: "+id+"  did: "+did);
                Mono<List<RolePrivilegePo>> rolePrivilegePos = rolePrivilegeRepository.findAllByRoleId(id).collect(Collectors.toList());
                Mono<List<UserRolePo>> userRolePos = userRoleRepository.findByRoleId(id).collect(Collectors.toList());
                Mono<List<UserProxyPo>> userProxyPos = userProxyRepository.findAllByUserBId(id).collect(Collectors.toList());
                return Mono.zip(rolePrivilegePos,userRolePos,userProxyPos,roleRepository.deleteByIdAndDepartId(id,did)).map(tuple->{
                    System.out.println("zip");
                    logger.debug("deleteRole: delete role-privilege num = " + tuple.getT1().size());
                    tuple.getT1().stream().forEach(rolePrivilegePo->rolePrivilegeRepository.deleteById(rolePrivilegePo.getId()));
//                        for (RolePrivilegePo rolePrivilegePo : tuple.getT1()) {
//                            rolePrivilegeRepository.deleteById(rolePrivilegePo.getId());
//                        }
                    //删除缓存中角色权限信息
                    redisTemplate.delete("r_" + id);
                    logger.debug("deleteRole: delete user-role num = " + tuple.getT2().size());
                    tuple.getT2().stream().forEach(userRolePo->{
                        //删除缓存中具有删除角色的用户权限
                        redisTemplate.delete("u_" + userRolePo.getUserId());
                        redisTemplate.delete("up_" + userRolePo.getUserId());
                        //查询当前所有有效的代理具有删除角色用户的代理用户
                        tuple.getT3().stream().forEach(userProxyPo->{
                            //删除缓存中代理了具有删除角色的用户的代理用户
                            redisTemplate.delete("u_" + userProxyPo.getUserAId());
                            redisTemplate.delete("up_" + userProxyPo.getUserAId());
                            ;
                        });
                        userRoleRepository.deleteById(userRolePo.getId());
                    });
//                        for (UserRolePo userRolePo : tuple.getT2()) {
//                            userRoleRepository.deleteById(userRolePo.getId());
//                            //删除缓存中具有删除角色的用户权限
//                            redisTemplate.delete("u_" + userRolePo.getUserId());
//                            redisTemplate.delete("up_" + userRolePo.getUserId());
//                            //查询当前所有有效的代理具有删除角色用户的代理用户
//                            for(UserProxyPo userProxyPo : tuple.getT3()){
//                                //删除缓存中代理了具有删除角色的用户的代理用户
//                                redisTemplate.delete("u_" + userProxyPo.getUserAId());
//                                redisTemplate.delete("up_" + userProxyPo.getUserAId());
//                            }
//                        }
                    return new ReturnObject<>();
                });
            }
        });
//        }
//        catch (DataAccessException e){
//            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
//            return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage())));
//        }
//        catch (Exception e) {
//            // 其他Exception错误
//            logger.error("other exception : " + e.getMessage());
//            return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage())));
//        }
    }

    public Mono<ReturnObject> updateRole(Role role){
//        try{
        return roleRepository.findByIdAndDepartId(role.getId(),role.getDepartId()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional->{
            if (!resOptional.isPresent()) {
                //修改失败
                logger.debug("updateRole: update role fail : " + role.gotRolePo().toString());
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("角色id不存在：" + role.gotRolePo().getId())));
            }else {
                //修改成功
                logger.debug("updateRole: update role = " + role.gotRolePo().toString());
                System.out.println("updateRolePo");
                return roleRepository.save(role.gotRolePo()).map(res->new ReturnObject<>());
            }
        });
//        }catch (DataAccessException e){
//            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
//                //若有重复的角色名则修改失败
//                logger.debug("updateRole: have same role name = " + rolePo.getName());
//                return  Mono.just(new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("角色名重复：" + rolePo.getName())));
//            } else {
//                // 其他数据库错误
//                logger.debug("other sql exception : " + e.getMessage());
//                return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage())));
//            }
//        }catch (Exception e) {
//            // 其他Exception错误
//            logger.error("other exception : " + e.getMessage());
//            return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage())));
//        }
    }


}
