package demo.model.bo;

import cn.edu.xmu.ooad.model.VoObject;

import demo.model.po.PrivilegePo;
import demo.model.po.RolePo;
import demo.model.po.UserPo;
import demo.model.vo.RolePrivilegeRetVo;
import lombok.Data;

/**
 * 角色权限
 * @author wc 24320182203277
 * @date
 **/

@Data
public class RolePrivilege implements VoObject {
    private Long id= null;
    private RolePo role = RolePo.builder().build();
    private PrivilegePo privilege = PrivilegePo.builder().build();
    private UserPo creator = UserPo.builder().build();
    private String gmtModified = null;
    @Override
    public RolePrivilegeRetVo createVo() {
        return new RolePrivilegeRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return new RolePrivilegeRetVo(this);
    }
}
