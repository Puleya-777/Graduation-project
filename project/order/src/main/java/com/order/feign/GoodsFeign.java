package com.order.feign;

import com.order.model.po.ShopPo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GoodsFeign {

    public static Mono<ShopPo> getById(Long id){
        ShopPo shopPo = new ShopPo();
        shopPo.setState(1);
        return Mono.just(shopPo);
    }

}
