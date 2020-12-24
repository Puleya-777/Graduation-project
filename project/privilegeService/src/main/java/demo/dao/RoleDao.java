package demo.dao;

import com.example.model.VoObject;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.example.util.encript.SHA256;
import com.github.pagehelper.PageInfo;
import demo.controller.PrivilegeController;
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
}
