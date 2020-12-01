package demo.Controller;

import io.swagger.annotations.*;
import cn.edu.xmu.ooad.annotation.Audit;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chei1
 */
@RestController
@RequestMapping(value = "/privilege", produces = "application/json;charset=UTF-8")
public class PrivilegeController {

    /***
     * 取消用户权限
     * @param id 用户id
     * @param did 部门id
     * @return
     */
    @ApiOperation(value = "取消用户权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name="id", value="角色id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="did", value="部门id", required = true, dataType="Integer", paramType="path")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @DeleteMapping("/shops/{did}/adminuserroles/{id}")
    public Object revokeRole(@PathVariable Long did, @PathVariable Long id){
        return null;
    }

}
