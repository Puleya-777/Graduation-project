package demo.service;

import com.example.model.VoObject;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import demo.dao.RoleDao;
import demo.model.bo.Role;
import demo.model.bo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RoleService {

    @Autowired
    RoleDao roleDao;

    public Mono<ReturnObject<PageInfo<VoObject>>> selectAllRoles(Long departId, Integer page, Integer pageSize) {
        return roleDao.selectAllRole(departId, page, pageSize);
    }

    public Mono<ReturnObject<VoObject>> insertRole(Role role) {
        return roleDao.insertRole(role).map(rolePo -> {
            if(rolePo != null) {
                role.setId(rolePo.getId());
                return new ReturnObject<>(role);
            } else {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        });
    }
}
