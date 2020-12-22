package demo.Controller;

import com.example.util.ReturnObject;
import com.example.util.encript.AES;
import demo.Repository.RoleRepository;
import demo.Repository.UserRepository;
import demo.dao.UserDao;
import demo.model.bo.User;
import demo.model.po.UserPo;
import demo.model.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@RestController
public class OkController {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDao userDao;

    @RequestMapping("/ok")
    public String ok() {
        return "ok";
    }

    @GetMapping("/getTest/{id}")
    public Mono testGet(@PathVariable Long id){
    return userRepository.findById(id).map(res->{
        System.out.println(res.getUserName());
        System.out.println(res.getId());
        return res;
    });
    }

    @PostMapping("/addTest")
    public Mono<UserPo> addTest(@RequestBody LoginVo loginVo){
        UserPo newPo = new UserPo();
        newPo.setUserName(loginVo.getUserName());
        newPo.setPassword(loginVo.getPassword());
        newPo.setGmtCreate(LocalDateTime.now());
        return userRepository.save(newPo);
    }

    @DeleteMapping("/deleteTest/{id}")
    public Mono<Void> deleteTest(@PathVariable Long id){
        return userRepository.deleteById(id);
    }

    @GetMapping("/test/{id}")
    public Mono<Boolean> test(@PathVariable Long id){
        return userDao.setLoginIPAndPosition(id,"localhost",LocalDateTime.now());

    }

}
