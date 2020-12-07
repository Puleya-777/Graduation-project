package demo.Controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import demo.repository.UserProxyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
@RestController
@RequestMapping(value = "/privilege", produces = "application/json;charset=UTF-8")
@Slf4j
public class PrivilegeController {

    @Autowired
    UserProxyRepository userProxyRepository;


    /**
     * 解除用户代理关系
     *
     * @param id
     * @return createdBy Di Han Li 2020/11/04 09:57
     */
    @Audit
    @DeleteMapping("proxie/{id}")
    public Mono removeUserProxy(@PathVariable Long id/**, @LoginUser @ApiIgnore Long userId**/) {
        Long userId=49L;
        return userProxyRepository.findById(id).map(it->{
            if(it.getUserAId().equals(userId)){
                userProxyRepository.deleteById(it.getId()).doOnError(err -> log.error(err.getMessage(), err));
                return Mono.just(new ReturnObject(ResponseCode.OK));
            }
            return null;
        }).defaultIfEmpty(Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE)));
    }
}
