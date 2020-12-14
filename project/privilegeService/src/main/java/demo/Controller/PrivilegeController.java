package demo.Controller;

import com.example.annotation.Audit;
import com.example.annotation.Depart;
import com.example.annotation.LoginUser;
import com.example.model.VoObject;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.example.util.encript.AES;
import com.github.pagehelper.PageInfo;
import demo.Repository.*;
import demo.model.bo.Role;
import demo.model.bo.User;
import demo.model.bo.UserRole;
import demo.model.po.PrivilegePo;
import demo.model.po.RolePo;
import demo.model.po.UserPo;
import demo.model.po.UserRolePo;
import demo.model.vo.PrivilegeVo;
import demo.model.vo.RoleVo;
import demo.service.RoleService;
import demo.service.UserService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chei1
 */
@RestController
@RequestMapping(value = "/privilege", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
public class PrivilegeController {

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @Resource
    private HttpServletResponse httpServletResponse;

    /***1
     * 取消用户权限
     * @param userid 用户id
     * @param roleid 角色id
     * @param did 部门id
     */
    @ApiOperation(value = "取消用户权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "id", value = "角色id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "did", value = "部门id", required = true, dataType = "Integer", paramType = "path")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @DeleteMapping("/shops/{did}/adminusers/{userid}/roles/{roleid}")
    public Mono<Object> revokeRole(@PathVariable Long did, @PathVariable Long userid, @PathVariable Long roleid) {

        return userService.revokeRole(userid,roleid,did).map(Common::decorateReturnObject);
    }

    /***2
     * 赋予用户权限
     * @param userid 用户id
     * @param roleid 角色id
     * @param createid 创建者id
     * @param did 部门id
     * @return
     */
    @ApiOperation(value = "赋予用户权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "userid", value = "用户id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "roleid", value = "角色id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "did", value = "部门id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
//    @Audit
    @PostMapping("/shops/{did}/adminusers/{userid}/roles/{roleid}")
    public Mono<Object> assignRole(@LoginUser Long createid, @PathVariable Long did, @PathVariable Long userid, @PathVariable Long roleid) {

        return userService.assignRole(createid, userid, roleid, did)
                .map(returnObject->{
                    if(returnObject.getCode()== ResponseCode.OK){
                        return Common.getRetObject(returnObject);
                    }else{
                        return Common.decorateReturnObject(returnObject);
                    }
                });

    }

    /***3
     * 获得自己角色信息
     * @return
     */
    @ApiOperation(value = "获得自己角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
//    @Audit
    @GetMapping(value = "/adminusers/self/roles/{id}")
    public @ResponseBody
    Mono<Object> getUserSelfRole(@PathVariable Long id) {
        Mono<ReturnObject<List>> mono=userService.getSelfUserRoles(id);
//        System.out.println(mono.block().getData());
        Mono<Object> ret=mono.map(Common::getListRetObject);
//        System.out.println(ret.block());
        return ret;
    }


    /***4
     * 获得所有人角色信息
     * @param id 用户id
     * @param did 部门id
     * @return
     */
    @ApiOperation(value = "获得所有人角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "did", value = "部门id", required = true, dataType = "int", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/shops/{did}/adminusers/{id}/roles")
    public Mono<Object> getSelfRole(@PathVariable Long did, @PathVariable Long id) {

        return userService.getUserRoles(id, did).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.getListRetObject(returnObject);
            } else {
                return Common.decorateReturnObject(returnObject);
            }
        });

    }

    /**
     * 5
     * 获得所有权限
     *
     * @return Object
     */
    @ApiOperation(value = "获得所有权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("privileges")
    public Mono<Object> getAllPrivs(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) {

        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;

        return userService.findAllPrivs(page, pageSize).map(Common::getPageRetObject);

    }

    /**
     * 6
     * 修改权限
     *
     * @param id : 权限id
     * @return Object
     */
    @ApiOperation(value = "修改权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, dataType = "String", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("privileges/{id}")
    public Mono<Object> changePriv(@PathVariable Long id, @Validated @RequestBody PrivilegeVo vo, BindingResult bindingResult, @LoginUser Long userId, @Depart Long departId,
                             HttpServletResponse httpServletResponse) {
        logger.debug("changePriv: id = " + id + " vo" + vo);
        logger.debug("getAllPrivs: userId = " + userId + " departId = " + departId);
        /* 处理参数校验错误 */
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (o != null) {
            return Mono.just(o);
        }

        return userService.changePriv(id, vo).map(returnObject -> {
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.getRetObject(returnObject);
            } else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

    /**
     * 7
     * auth007: 查询某一用户权限
     *
     * @param id
     * @return Object
     */
    @ApiOperation(value = "获得某一用户的权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "did", required = true, dataType = "String", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
//    @Audit // 需要认证
    @GetMapping("/shops/{did}/adminusers/{id}/privileges")
    public Mono<Object> getPrivsByUserId(@PathVariable Long did, @PathVariable Long id) {

        return userService.findPrivsByUserId(id,did).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.getListRetObject(returnObject);
            } else {
                return Common.decorateReturnObject(returnObject);
            }
        });

    }

    /**
     * 8
     *
     * @date Created in 2020/11/8 0:33
     **/
    @ApiOperation(value = "auth003:查看自己信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true)
    })
    @ApiResponses({
    })
    @Audit
    @GetMapping("adminusers")
    public Mono<Object> getUserSelf(@LoginUser Long userId) {
        logger.debug("getUserSelf userId:" + userId);

        return userService.findUserById(userId).map(returnObject -> {
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.getRetObject(returnObject);
            } else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

    /**
     * 9
     *
     * @date Created in 2020/11/8 0:33
     **/
//    @Audit
    @ApiOperation(value = "auth003: 查看任意用户信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "用户id", required = true)
    })
    @ApiResponses({
    })
    @GetMapping("/shops/{did}/adminusers/{id}")
    public Mono<Object> getUserById(@PathVariable("id") Long id) {

        return userService.findUserById(id).map(returnObject->{
            if (!returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
                return Common.getRetObject(returnObject);
            } else {
                return Common.getNullRetObj(new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg()), httpServletResponse);
            }
        });

//        Mono<Object> userPoMono = userRepository.findById(id).map(userPo ->
//                userPo == null ? new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST) : new ReturnObject<>(new User(userPo))
//        );
//
//        return userPoMono;
    }


    /**
     * 10
     *
     * @date Created in 2020/11/8 0:33
     **/
//    @Audit
    @ApiOperation(value = "auth003: 查询用户信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "userName", value = "用户名", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "mobile", value = "电话号码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pagesize", value = "每页数目", required = true)
    })
    @ApiResponses({
    })
    @GetMapping(value = "/shops/{did}/adminusers/all")
    public Mono<Object> findAllUser(
            @RequestParam String userName,
            @RequestParam String mobile,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pagesize) {

        if(page <= 0 || pagesize <= 0) {
            return Mono.just(Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse));
        }else{
            return userService.findAllUsers(userName, mobile, page, pagesize).map(Common::getPageRetObject);
        }

    }

    /**
     * 11
     * 分页查询所有角色
     *
     * @param page     页数
     * @param pageSize 每页大小
     * @return Object 角色分页查询结果
     */
    @ApiOperation(value = "查询角色", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "did", value = "部门id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/shops/{did}/roles")
    public Mono<Object> selectAllRoles(@LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                                 @Depart @ApiIgnore @RequestParam(required = false) Long departId,
                                 @PathVariable("did") Long did,
                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                 @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        logger.debug("selectAllRoles: page = "+ page +"  pageSize ="+pageSize);
        if(did.equals(departId)){
            return roleService.selectAllRoles(departId, page, pageSize).map(Common::getPageRetObject);
        }
        else{
            return Mono.just(Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("部门id不匹配：" + did)), httpServletResponse));
        }

//        if(did.equals(departId)){
//            ReturnObject<PageInfo<VoObject>> returnObject =  roleService.selectAllRoles(departId, page, pageSize);
//            return Common.getPageRetObject(returnObject);
//        }
//        else{
//            return Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("部门id不匹配：" + did)), httpServletResponse);
//        }
    }

    /**
     * 12
     * 新增一个角色
     *
     * @param vo            角色视图
     * @param bindingResult 校验错误
     * @param userId        当前用户id
     * @return Object 角色返回视图
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @ApiOperation(value = "新增角色", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "RoleVo", name = "vo", value = "可修改的用户信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 736, message = "角色名已存在"),
    })
    @Audit
    @PostMapping("/roles")
    public Mono<Object> insertRole(@Validated @RequestBody RoleVo vo, BindingResult bindingResult,
                             @LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                             @Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("insert role by userId:" + userId);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.debug("validate fail");
            return Mono.just(returnObject);
        }

        Role role = vo.createRole();
        role.setCreatorId(userId);
        role.setDepartId(departId);
        role.setGmtCreate(LocalDateTime.now());
        return roleService.insertRole(role).map(retObject->{
            if (retObject.getData() != null) {
                httpServletResponse.setStatus(HttpStatus.CREATED.value());
                return Common.getRetObject(retObject);
            } else {
                return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
            }
        });
    }



        @GetMapping("/test")
    public Mono<Object> test(){

        return null;
    }

}
