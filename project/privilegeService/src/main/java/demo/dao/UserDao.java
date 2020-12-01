package demo.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import demo.model.bo.User;
import demo.model.po.NewUserPo;
import demo.model.po.UserPo;
import demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author chei1
 */
@Repository
public class UserDao {

    @Autowired
    UserRepository userRepository;

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

}
