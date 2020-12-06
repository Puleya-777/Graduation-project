package demo.Service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import demo.dao.RoleDao;
import demo.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chei1
 */
@Service
public class RoleService {

    @Autowired
    RoleDao roleDao;
    @Autowired
    UserDao userDao;


    /**
     * 增加角色权限
     * @param roleid 角色id
     * @param privid 权限id
     * @param userid 用户id
     * @return 权限列表
     */
    @Transactional
    public ReturnObject<VoObject> addRolePriv(Long roleid, Long privid, Long userid){
        //新增
        ReturnObject<VoObject> ret = roleDao.addPrivByRoleIdAndPrivId(roleid, privid, userid);
        //新增成功，缓存中干掉用户
        if(ret.getCode()== ResponseCode.OK) {
            clearuserByroleId(roleid);
        }
        return ret;
    }
    @Transactional
    public void clearuserByroleId(Long id){
        userDao.clearUserByRoleId(id);
    }


}
