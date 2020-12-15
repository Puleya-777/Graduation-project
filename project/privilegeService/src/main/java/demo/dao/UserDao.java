package demo.dao;

import com.example.model.VoObject;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import demo.Repository.*;
import demo.model.bo.Role;
import demo.model.bo.User;
import demo.model.bo.UserRole;
import demo.model.po.RolePo;
import demo.model.po.UserPo;
import demo.model.po.UserRolePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
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

    public Mono<Boolean> checkUserDid(Long userid, Long departid) {
        return userRepository.findById(userid).map(userPo -> {
            if(userPo==null||userPo.getDepartId()!=departid){
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
        Mono<UserPo> userPoMono=userRepository.findById(userid);
        Mono<RolePo> rolePoMono=roleRepository.findById(roleid);
        Mono<Integer> deleteResult=userRoleRepository.deleteUserRolePoByUserIdAndAndRoleId(userid,roleid);
        return Mono.zip(userPoMono,rolePoMono,deleteResult).map(tuple->{
            if(tuple.getT1()==null||tuple.getT2()==null){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else if(tuple.getT3()==0){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else{
                return new ReturnObject<>();
            }
        });
    }

    public Mono<Boolean> checkRoleDid(Long roleid, Long departId) {
        return roleRepository.findById(roleid).map(rolePo -> {
            if(rolePo==null||rolePo.getDepartId()!=departId){
                return false;
            }else{
                return true;
            }
        });
    }


    public Mono<ReturnObject<VoObject>> assignRole(Long createid, Long userid, Long roleid) {
        Mono<UserPo> userPoMono=userRepository.findById(userid);
        Mono<UserPo> createMono=userRepository.findById(createid);
        Mono<RolePo> rolePoMono=roleRepository.findById(roleid);

        Mono<UserRolePo> userRolePoMono=userRoleRepository.findByUserIdAndRoleId(userid,roleid);
        return Mono.zip(userPoMono,createMono,rolePoMono,userRolePoMono).map(tuple->{
            if(tuple.getT1()==null||tuple.getT2()==null||tuple.getT3()==null){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else if(userRolePoMono==null){
                logger.warn("assignRole: 该用户已拥有该角色 userid=" + userid + "roleid=" + roleid);
                return new ReturnObject<>(ResponseCode.USER_ROLE_REGISTERED);
            }else{
                UserRolePo userRolePo = new UserRolePo();
                userRolePo.setUserId(userid);
                userRolePo.setRoleId(roleid);
                userRolePo.setCreatorId(createid);
                userRolePo.setGmtCreate(LocalDateTime.now());
                UserRole userRole = new UserRole(userRolePo, new User(tuple.getT1()), new Role(tuple.getT3()), new User(tuple.getT2()));
                userRolePo.setSignature(userRole.getCacuSignature());

                userRoleRepository.save(userRolePo);
                return new ReturnObject<>(userRole);
            }
        });
    }

    public Mono<ReturnObject<List>> getUserRoles(Long id) {
        Mono<User> userMono=userRepository.findById(id).map(User::new);
        Flux<UserRole> userRoleFlux=Flux.empty();
        userMono.map(user -> {
                return userRoleRepository.findAllByUserId(id).map(
                        userRolePo -> {
                            Mono<User> creatorMono=userRepository.findById(userRolePo.getCreatorId()).map(User::new);
                            Mono<Role> roleMono=roleRepository.findById(userRolePo.getRoleId()).map(Role::new);
                            Mono.zip(creatorMono,roleMono).map(tuple->{
                                if(tuple.getT2()!=null&&tuple.getT1()!=null){
                                    UserRole userRole = new UserRole(userRolePo, user, tuple.getT2(), tuple.getT1());
                                    //校验签名
                                    if (userRole.authetic()){
                                        userRoleFlux.concatWithValues(userRole);
                                        logger.info("getRoleIdByUserId: userId = " + userRolePo.getUserId() + " roleId = " + userRolePo.getRoleId());
                                    } else {
                                        logger.error("getUserRoles: Wrong Signature(auth_user_role): id =" + userRolePo.getId());
                                    }
                                }
                                return tuple;
                            });
                            return userRolePo;
                        }

                );
            }

        );
        return userRoleFlux.collect(Collectors.toList()).map(ReturnObject::new);
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
                    .flatMap(userRolePo -> rolePrivilegeRepository.findById(userRolePo.getRoleId()))
                    .flatMap(rolePrivilegePo -> privilegeRepository.findById(rolePrivilegePo.getPrivilegeId()))
                    .collect(Collectors.toList()).map(ReturnObject::new);
        });
    }

    public Mono<UserPo> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<PageInfo<UserPo>> findAllUsers(String userNameAES, String mobileAES, Integer page, Integer pagesize) {
        return userRepository.findAllByUserNameAndMobile(userNameAES,mobileAES)
                .collect(Collectors.toList()).map(PageInfo::new);
    }
}
