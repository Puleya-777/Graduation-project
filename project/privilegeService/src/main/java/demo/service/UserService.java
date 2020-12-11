package demo.service;

import com.example.model.VoObject;
import com.example.util.Common;
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
import demo.model.vo.PrivilegeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
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
        Mono<PageInfo<UserPo>> userPos = userDao.findAllUsers(userNameAES, mobileAES, page, pagesize);
        Mono<List<VoObject>> users=userPos.map(pageInfo->pageInfo.getList().stream().map(User::new)
                .filter(User::authetic).collect(Collectors.toList()));
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
}
