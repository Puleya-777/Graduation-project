package demo.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.SHA256;
import demo.model.bo.Role;
import demo.model.bo.RolePrivilege;
import demo.model.po.*;
import demo.repository.PrivilegeRepository;
import demo.repository.RolePrivilegeRepository;
import demo.repository.RoleRepository;
import demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chei1
 */
@Repository
public class RoleDao {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PrivilegeRepository privilegeRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    RolePrivilegeRepository rolePrivilegeRepository;


    /**
     * 由Role Id, Privilege Id 增加 角色权限
     *
     * @param  roleId, PrivilegeId, userid
     * @return RolePrivilegeRetVo
     */
    public ReturnObject<VoObject> addPrivByRoleIdAndPrivId(Long roleId, Long privId, Long userId){
        UserPo userpo = userRepository.findById(userId).block();
        PrivilegePo privilegepo = privilegeRepository.findById(privId).block();
        RolePo rolePo = roleRepository.findById(roleId).block();
        if(userpo==null || privilegepo==null || rolePo==null){
            return new ReturnObject<VoObject>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        RolePrivilege rolePrivilege = new RolePrivilege();

        //查询是否角色已经存在此权限
//        RolePrivilegePoExample example = new RolePrivilegePoExample();
//        RolePrivilegePoExample.Criteria criteria = example.createCriteria();
//        criteria.andPrivilegeIdEqualTo(privid);
//        criteria.andRoleIdEqualTo(roleid);
        //List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(example);
        List<RolePrivilegePo> rolePrivilegePos = rolePrivilegeRepository.findAllByPrivilegeIdAndRoleId(privId,roleId)
                .collectList().block();
        RolePrivilegePo roleprivilegepo = RolePrivilegePo.builder().build();

        if(rolePrivilegePos.isEmpty()){
            roleprivilegepo.setRoleId(roleId);
            roleprivilegepo.setPrivilegeId(privId);
            roleprivilegepo.setCreatorId(userId);
            roleprivilegepo.setGmtCreate(localDateTime);

            StringBuilder signature = Common.concatString("-", roleprivilegepo.getRoleId().toString(),
                    roleprivilegepo.getPrivilegeId().toString(), roleprivilegepo.getCreatorId().toString(), localDateTime.toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            roleprivilegepo.setSignature(newSignature);

            try {
                //int ret = rolePrivilegePoMapper.insert(roleprivilegepo);
                RolePrivilegePo ret=rolePrivilegeRepository.save(roleprivilegepo).block();
                if (ret == null) {
                    //插入失败
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                } else {
                    //插入成功
                    //清除角色权限
                    String key = "r_" + roleId;
                    if(redisTemplate.hasKey(key)){
                        redisTemplate.delete(key);
                    }
                }
            }catch (DataAccessException e){
                // 数据库错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }catch (Exception e) {
                // 其他Exception错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
            }

        }else{
//            FIELD_NOTVALID
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("角色权限已存在"));
        }

        //组装返回的bo
        rolePrivilege.setId(roleprivilegepo.getId());
        rolePrivilege.setCreator(userpo);
        rolePrivilege.setRole(rolePo);
        rolePrivilege.setPrivilege(privilegepo);
        rolePrivilege.setGmtModified(localDateTime.toString());

        return new ReturnObject<VoObject>(rolePrivilege);
    }
}
