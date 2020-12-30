package demo.controller;

//import com.example.annotation.Audit;
//import com.example.annotation.Depart;
//import com.example.annotation.LoginUser;
import demo.annotation.Audit;
import demo.annotation.Depart;
import demo.annotation.LoginUser;

import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ResponseUtil;
import com.example.util.ReturnObject;
import demo.model.po.UserPo;
import demo.repository.*;
import demo.model.bo.Role;
import demo.model.bo.User;
import demo.model.vo.*;
import demo.service.NewUserService;
import demo.service.RoleService;
import demo.service.UserProxyService;
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
import demo.util.IpUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    UserProxyService userProxyService;
    @Autowired
    NewUserService newUserService;
    private final UserProxyRepository userProxyRepository;

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final RoleRepository roleRepository;

    private final PrivilegeRepository privilegeRepository;

    private final RolePrivilegeRepository rolePrivilegeRepository;

    public PrivilegeController(UserProxyRepository userProxyRepository, UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, RolePrivilegeRepository rolePrivilegeRepository) {
        this.userProxyRepository = userProxyRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.rolePrivilegeRepository = rolePrivilegeRepository;
    }

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
//            return userRoleRepository.deleteUserRolePoByUserIdAndAndRoleId(userid,roleid);
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
    @Audit
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
    @Audit
    @GetMapping(value = "/adminusers/self/roles/{id}")
    public @ResponseBody
    Mono<Object> getUserSelfRole(@PathVariable Long id) {
        return userService.getSelfUserRoles(id).map(Common::getListRetObject);
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
     * TODO
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
    public Object getAllPrivs(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) {
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;

        return userService.findAllPrivs(page, pageSize).map(Common::getPageRetObject);


    }

    /**
     * 以下这个url运行有问题，先注释掉；
     */

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
    @Audit // 需要认证
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
    @Audit
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

    }


    /**
     * 10
     *
     * @date Created in 2020/11/8 0:33
     **/
    @Audit
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

    }

    /**
     * 12
     * 新增一个角色
     *
     * @param vo            角色视图
     * @param bindingResult 校验错误
     * @param userId        当前用户id
     * @return Object 角色返回视图
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
//        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
//        if (null != returnObject) {
//            logger.debug("validate fail");
//            return Mono.just(returnObject);
//        }

        Role role = vo.createRole();
        role.setCreatorId(userId);
        role.setDepartId(departId);
        role.setGmtCreate(LocalDateTime.now());
        return roleService.insertRole(role).map(retObject->{
            if (retObject.getData() != null) {
//                httpServletResponse.setStatus(HttpStatus.CREATED.value());
                return Common.getRetObject(retObject);
            } else {
                return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
            }
        });
    }

        @GetMapping("/test")
    public void test(){
        System.out.println(new UserPo());
    }


    /**
     * huiyu
     */

    /**
     * 解除用户代理关系(pass)
     */
    @ApiOperation(value = "解除用户代理关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("proxies/{id}")
    public Mono removeUserProxy(@PathVariable Long id, @LoginUser @ApiIgnore Long userId) {
        logger.info("removeUserProxy: id = " + id);
        return userProxyService.removeUserProxy(id, userId);
    }

    /**
     * 查询所有用户代理关系
     */
    @ApiOperation(value = "查询所有用户代理关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    //@Audit
    @GetMapping("shops/{did}/proxies")
    public Mono listProxies(Long aId, Long bId,@PathVariable Long did) {
        logger.debug("listProxies: aId = " + aId + " bId = " + bId);
        return userProxyService.listProxies(aId, bId,did);
    }

    /**
     * 禁止代理关系(pass)
     */
    @ApiOperation(value = "禁止代理关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("shops/{did}/proxies/{id}")
    public Mono removeAllProxies(@PathVariable Long id,@PathVariable Long did) {
        logger.debug("removeAllProxies: id) = " + id);
        return userProxyService.removeAllProxies(id,did);
    }

    /**
     * 注册用户(pass?bloom过滤器部分)
     * @return
     */
    @ApiOperation(value="注册用户")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "NewUserVo", name = "vo", value = "newUserInfo", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 404, message = "参数不合法")
    })
    @PostMapping("adminusers")
    public Mono register(@Validated @RequestBody NewUserVo vo, BindingResult result){
        if(result.hasErrors()){
            return Mono.just(Common.processFieldErrors(result,httpServletResponse));
        }
        return newUserService.registerCheck(vo).map(ret->{
            if(ret.getCode()==ResponseCode.OK){
                return Mono.just(ResponseUtil.ok(ret.getData()));
            }
            else {
                return Mono.just(ResponseUtil.fail(ret.getCode()));
            }
        });
    }
    /**
     * 查询所有状态(pass)
     * @return Object
     */
    @ApiOperation(value="获得管理员用户的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @GetMapping("adminusers/states")
    public Object getAllStates(){
        User.State[] states=User.State.class.getEnumConstants();
        List<StateVo> stateVos=new ArrayList<StateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new StateVo(states[i]));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    }

    /**
     * auth004: 修改自己的信息(pass)
     */
    @ApiOperation(value = "修改自己的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "RoleVo", name = "vo", value = "可修改的用户信息", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PutMapping("adminusers")
    public Mono changeMyAdminselfInfo(@LoginUser Long id, @Validated @RequestBody UserVo vo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return Mono.just(Common.processFieldErrors(bindingResult,httpServletResponse));
        }
        logger.info("modifyUserInfo: id = "+ id +" vo = " + vo);
        if (logger.isDebugEnabled()) {
            logger.info("modifyUserInfo: id = "+ id +" vo = " + vo);
        }
//        return Mono.just(Common.processFieldErrors(bindingResult, httpServletResponse)).map(it->{
//            if(it!=null){
//                logger.info("incorrect data received while modifyUserInfo id = " + id);
//                return it;
//            }else{
//                //return Common.decorateReturnObject();
                return userService.modifyUserInfo(id, vo).map(Common::decorateReturnObject);
//            }
//        });
    }

    /**
     * auth002: 用户重置密码(pass)
     */
    @ApiOperation(value="用户重置密码")
    @ApiResponses({
            @ApiResponse(code = 745, message = "与系统预留的邮箱不一致"),
            @ApiResponse(code = 746, message = "与系统预留的电话不一致"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("adminusers/password/reset")
    @ResponseBody
    public Mono resetPassword(@RequestBody ResetPwdVo vo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {

        if (logger.isDebugEnabled()) {
            logger.debug("resetPassword");
        }
//        /* 处理参数校验错误 */
//        return Mono.just(Common.processFieldErrors(bindingResult, httpServletResponse)).map(o->{
//            if(o!=null){
//                return o;
//            }else{
                String ip = IpUtil.getIpAddr(httpServletRequest);
                return userService.resetPassword(vo,ip).map(it-> Common.decorateReturnObject(it));
//            }
//        });
    }

    /**
     * auth002: 用户修改密码
     */
    @ApiOperation(value="用户修改密码",produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 700, message = "用户名不存在或者密码错误"),
            @ApiResponse(code = 741, message = "不能与旧密码相同"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("/adminusers/password")
    @ResponseBody
    public Mono modifyPassword(@RequestBody ModifyPwdVo vo) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyPassword");
        }
        return userService.modifyPassword(vo).map(it-> Common.decorateReturnObject(it));
    }

    /**
     * 获得角色所有权限(pass)
     */
    @ApiOperation(value = "获得角色所有权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("roles/{id}/privileges")
    public Mono getRolePrivs(@PathVariable Long id){
        return roleService.findRolePrivs(id).map(ret->{
            if (ret.getCode() == ResponseCode.OK) {
                return Common.getListRetObject(ret);
            } else {
                return Common.decorateReturnObject(ret);
            }
        });
    }

    /**
     * 取消角色权限(pass)
     */
    @ApiOperation(value = "取消角色权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @DeleteMapping("shops/{did}/roleprivileges/{id}")
    public Mono delRolePriv(@PathVariable Long did,@PathVariable Long id){
        logger.info("delRolePriv: id = "+ id);
        /**
         * 这边很奇怪，默认return fail
         */
        return roleService.delRolePriv(id).map(ret -> ResponseUtil.fail(ret.getCode(), ret.getErrmsg()));
        
    }

    /**
     * 增加角色权限(pass)
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
    @PostMapping("roles/{roleid}/privileges/{privilegeid}")
    public Mono addRolePriv(@PathVariable Long roleid, @PathVariable Long privilegeid, @LoginUser @ApiIgnore @RequestParam(required = false, defaultValue = "1") Long userId){
        logger.info("addRolePriv: id = "+ roleid+" userid: id = "+ userId);

       return roleService.addRolePriv(roleid, privilegeid, userId).map(returnObject -> {
           if (returnObject.getCode() == ResponseCode.OK) {
               return Common.getRetObject(returnObject);
           } else {
               return Common.decorateReturnObject(returnObject);
           }
       });

    }

    /**
     * auth014: 管理员审核用户(pass)
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
    public Mono approveUser(@PathVariable Long id,@PathVariable Long did,@RequestBody Boolean approve,@Depart Long shopid) {
        logger.info("approveUser: did = "+ did+" userid: id = "+ id+" opinion: "+approve);
        if(did==0|| did.equals(shopid))
        {
            return newUserService.approveUser(approve,id);
        }
        else
        {
            logger.error("approveUser: 无权限查看此部门的用户 did=" + did);
            return Mono.just(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
        }
    }

    /**
     * 获取新用户列表(pass?使用Flux,需前后端联调)
     */
    @GetMapping(value = "shops/{did}/adminusers/allnew",produces =  MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono getNewUser(@PathVariable Long did){
        return newUserService.getAllNewUser(did).collectList().map(it-> new ReturnObject<>(it));
    }

    @ApiOperation(value = "登录")//k
    @PostMapping("/privileges/login")
    public Mono login(@Validated @RequestBody LoginVo loginVo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest){
        /* 处理参数校验错误 */
        System.out.println("1");
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return Mono.just(o);
        }
        System.out.println("2");
        String ip = IpUtil.getIpAddr(httpServletRequest);
        System.out.println("3");
        return userService.login(loginVo.getUserName(), loginVo.getPassword(), ip).map(jwt->{
            System.out.println("controller-1");
            if(jwt.getData() == null){
                return ResponseUtil.fail(jwt.getCode(), jwt.getErrmsg());
            }else{
                return ResponseUtil.ok(jwt.getData());
            }
        });
    }

    //13
    @ApiOperation(value = "删除角色", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "did", value = "部门id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "角色id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @com.example.annotation.Audit
    @DeleteMapping("/shops/{did}/roles/{id}")
    public Mono deleteRole(@PathVariable("did") Long did, @PathVariable("id") Long id,
                           @com.example.annotation.LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                           @com.example.annotation.Depart @ApiIgnore @RequestParam(required = false) Long departId){
        logger.debug("delete role");
        if(did.equals(departId)){
            System.out.println("controller-deleteRole");
            return roleService.deleteRole(departId, id).map(Common::decorateReturnObject);
        }else{
            return Mono.just(Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("部门id不匹配：" + did)), httpServletResponse));
        }
    }

    //14
    @ApiOperation(value = "修改角色信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "did", value = "部门id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "角色id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "RoleVo", name = "vo", value = "可修改的用户信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 736, message = "角色名已存在"),
    })
    @com.example.annotation.Audit
    @PutMapping("/shops/{did}/roles/{id}")
    public Mono updateRole(@PathVariable("did") Long did, @PathVariable("id") Long id, @Validated @RequestBody RoleVo vo, BindingResult bindingResult,
                           @com.example.annotation.LoginUser @ApiIgnore @RequestParam(required = false) Long userId,
                           @com.example.annotation.Depart @ApiIgnore @RequestParam(required = false) Long departId) {
        logger.debug("update role by userId:" + userId);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return Mono.just(returnObject);
        }
        if(did.equals(departId)){
            Role role = vo.createRole();
            role.setId(id);
            role.setDepartId(departId);
            role.setCreatorId(userId);
            role.setGmtModified(LocalDateTime.now());
            System.out.println("controller-14");
            return roleService.updateRole(role).map(retObject->{
                if(retObject.getData()!= null)
                {
                    return retObject;
                }else{
                    return Common.getNullRetObj(new ReturnObject<>(retObject.getCode(), retObject.getErrmsg()), httpServletResponse);
                }
            });
        }
        else {
            return Mono.just(Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("部门id不匹配：" + did)), httpServletResponse));
        }
    }

    //15
    @ApiOperation(value = "修改任意用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @com.example.annotation.Audit // 需要认证
    @PutMapping("adminusers/{id}")
    public Mono modifyUserInfo(@PathVariable Long id, @Validated @RequestBody UserVo vo, BindingResult bindingResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyUserInfo: id = "+ id +" vo = " + vo);
        }
        // 校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (returnObject != null) {
            logger.info("incorrect data received while modifyUserInfo id = " + id);
            return Mono.just(returnObject);
        }
        System.out.println("controller-15");
        return userService.modifyUserInfo(id,vo).map(Common::decorateReturnObject);
    }

    //16
    @ApiOperation(value = "删除任意用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @com.example.annotation.Audit // 需要认证
    @DeleteMapping("/adminusers/{id}")
    public Mono deleteUser(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("deleteUser: id = "+ id);
        }
        System.out.println("controller-16");
        return userService.deleteUser(id).map(Common::decorateReturnObject);
    }

    //17
    @ApiOperation(value = "禁止用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @com.example.annotation.Audit // 需要认证
    @PutMapping("adminusers/{id}/forbid")
    public Mono forbidUser(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("forbidUser: id = "+ id);
        }
        System.out.println("controller-17");
        return userService.forbidUser(id).map(Common::decorateReturnObject);
    }

    //18
    @ApiOperation(value = "恢复用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @com.example.annotation.Audit // 需要认证
    @PutMapping("adminusers/{id}/release")
    public Mono releaseUser(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("releaseUser: id = "+ id);
        }
        System.out.println("controller-18");
        return userService.releaseUser(id).map(Common::decorateReturnObject);
    }

    //20 k
    @ApiOperation(value = "注销")
    @com.example.annotation.Audit
    @GetMapping("privileges/logout")
    public Mono logout(@com.example.annotation.LoginUser Long userId){
        logger.debug("logout: userId = "+userId);
        System.out.println("controller-20");
        return userService.Logout(userId).map(success->{
            if (success.getData() == null){
                return ResponseUtil.fail(success.getCode(), success.getErrmsg());
            }else {
                return ResponseUtil.ok();
            }
        });
    }

    //22
    @ApiOperation(value = "设置用户代理关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @com.example.annotation.Audit
    @PostMapping("users/{id}/proxy")
    public Mono usersProxy(@com.example.annotation.LoginUser @ApiIgnore  Long userId, @com.example.annotation.Depart @ApiIgnore Long departid, @PathVariable Long id, @Validated @RequestBody UserProxyVo vo, BindingResult bindingresult) {
        logger.debug("usersProxy: id = " + id + " vo" + vo);
        Object returnObject = Common.processFieldErrors(bindingresult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return Mono.just(returnObject);
        }
        System.out.println("controller-22");
        return userProxyService.usersProxy(userId, id, vo,departid);
    }

    @ApiOperation(value = "管理员设置用户代理关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "Token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "aid", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "bid", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PostMapping("ausers/{aid}/busers/{bid}")
    public Mono aUsersProxy(@PathVariable Long aid, @PathVariable Long bid,@Validated @RequestBody UserProxyVo vo, BindingResult bindingresult,@com.example.annotation.Depart @ApiIgnore Long departid) {
        Object returnObject = Common.processFieldErrors(bindingresult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return Mono.just(returnObject);
        }
        System.out.println("controller-23");
        return userProxyService.aUsersProxy(aid, bid, vo,departid);
    }

}
