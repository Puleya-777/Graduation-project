package demo.user.controller;

import com.example.annotation.Audit;
import com.example.util.ReturnObject;
import com.example.annotation.LoginUser;
import demo.user.model.po.UserPo;
import demo.user.model.vo.*;
import demo.user.service.UserService;
import demo.util.StateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chei1
 */
@RestController
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/users/states")
    public Mono getState(){
        List<StateVo> list=new ArrayList<>();
        for(Integer a: UserPo.stateMap.keySet()){
            list.add(new StateVo(a, UserPo.stateMap.get(a)));
        }
        return Mono.just(new ReturnObject<>(list));
    }

    @PostMapping("/users")
    public Mono registered(@RequestBody RegisteredVo vo){
        return userService.registered(vo);
    }

    @PostMapping("/users/login")
    public Mono login(@RequestBody LoginVo vo){
        return userService.login(vo.getUserName(),vo.getPassword());
    }
    @GetMapping("/users/logout")
    @Audit
    public Mono logout(@LoginUser Long userId){
        return userService.logout(userId);
    }

    @GetMapping("/users")
    @Audit
    public Mono getUser(@LoginUser Long userId){
        log.info("CustomerId:"+userId);
        return userService.getUser(userId);
    }

    @PutMapping("/users")
    @Audit
    public Mono ModifiedUser(@LoginUser Long userId, @RequestBody ModifiedUserVo vo){

        return userService.ModifiedUser(userId,vo);
    }

    @PutMapping("/users/password/reset")
    public Mono resetPassword(@RequestBody ResetPasswordVo vo){
        return userService.ResetPwd(vo);
    }

    @PutMapping("/users/password")
    public Mono modifiedPwd(@RequestBody ModifiedPwdVo vo){
        return userService.modifiedPwd(vo);
    }

    @GetMapping("/users/all")
    @Audit
    public Mono getAllUser(@RequestParam(required = false) String email,
                           @RequestParam(required = false) String mobile,
                           @RequestParam(required = false) String userName,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer pageSize){
        return userService.getAllUser(userName,mobile,email,page,pageSize);
    }

    @GetMapping("/users/{id}")
    @Audit
    public Mono getById(@PathVariable Long id){
        return userService.getUser(id);
    }

    @PutMapping("/shops/{did}/users/{id}/ban")
    @Audit
    public Mono banUser(@PathVariable Long did,@PathVariable Long id){
        return userService.banUser(id);
    }

    @PutMapping("/shops/{did}/users/{id}/release")
    @Audit
    public Mono releaseUser(@PathVariable Long did,@PathVariable Long id){
        return userService.releaseUser(id);
    }
}
