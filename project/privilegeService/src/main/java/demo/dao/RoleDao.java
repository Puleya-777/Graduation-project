package demo.dao;

import com.example.model.VoObject;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import demo.Repository.RoleRepository;
import demo.model.bo.Role;
import demo.model.po.RolePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RoleDao {

    @Autowired
    RoleRepository roleRepository;

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

    public Mono<RolePo> insertRole(Role role) {
        return roleRepository.save(role.gotRolePo());
    }
}
