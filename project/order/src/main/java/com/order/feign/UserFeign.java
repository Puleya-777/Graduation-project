package com.order.feign;

import com.order.model.po.UserPo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserFeign {

    public Mono<UserPo> getById(Long id){
        UserPo userPo = new UserPo();
        userPo.setUserName("昵称");
        userPo.setRealName("真实姓名");
//        if(userPo == null) System.out.println("userPo为空");
//        else System.out.println("userPo不是空的");
        return Mono.just(userPo);
    };
}
