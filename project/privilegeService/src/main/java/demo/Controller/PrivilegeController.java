package demo.Controller;

import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import demo.Service.NewUserService;
import demo.Service.RoleService;
import io.swagger.annotations.*;
import cn.edu.xmu.ooad.annotation.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author chei1
 */
@RestController
@RequestMapping(value = "/privilege", produces = "application/json;charset=UTF-8")
public class PrivilegeController {

    @Autowired
    NewUserService newUserService;

    @Autowired
    RoleService roleService;

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

    /**
     * 增加角色权限
     */
    @ApiOperation(value = "新增角色权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="roleid", required = true, dataType="String", paramType="path"),
            @ApiImplicitParam(name="privilegeid", required = true, dataType="String", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping(value = "roles/{roleid}/privileges/{privilegeid}",produces =  MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Object addRolePriv(@PathVariable Long roleid, @PathVariable Long privilegeid, @LoginUser @ApiIgnore @RequestParam(required = false, defaultValue = "0") Long userId){
        ReturnObject<VoObject> returnObject = roleService.addRolePriv(roleid, privilegeid, userId);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * auth014: 管理员审核用户
     */
    @ApiOperation(value = "管理员审核用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="did", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="approve", required = true, dataType="Boolean", paramType="body")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 503, message = "字段不合法"),
            @ApiResponse(code = 705, message = "无权限访问")
    })
    @Audit // 需要认证
    @PutMapping(value = "shops/{did}/adminusers/{id}/approve",produces =  MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<ReturnObject> approveUser(@PathVariable Long id, @PathVariable Long did/**, @RequestBody Boolean approve, @Depart Long shopid, BindingResult **/) {

        boolean approve=false;

        if(did==0/**|| did.equals(shopid)**/)
        {
            return newUserService.approveUser(approve,id);
        }
        else
        {
            return Mono.just(new ReturnObject(ResponseCode.FIELD_NOTVALID));
        }
    }

}
