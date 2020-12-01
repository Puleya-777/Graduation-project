package demo.Controller;

import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import demo.Service.NewUserService;
import io.swagger.annotations.*;
import cn.edu.xmu.ooad.annotation.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
@RestController
@RequestMapping(value = "/privilege", produces = "application/json;charset=UTF-8")
public class PrivilegeController {

    @Autowired
    NewUserService newUserService;

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
     * auth014: 管理员审核用户
     * @param id: 用户 id
     * @param bindingResult 校验信息
     * @return Object
     * @author 24320182203227 LiZihan
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
    @PutMapping("shops/{did}/adminusers/{id}/approve")
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
