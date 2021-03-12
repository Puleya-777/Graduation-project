package demo.cart.controller;

import demo.cart.model.vo.NewCartVo;
import demo.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
@RestController
@Slf4j
public class CartController {
    @Autowired
    CartService cartService;

    @GetMapping("carts")
    /**
     * TODO 解析Token获取customerId
     */
    public Mono getCarts(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer pageSize){
        return cartService.getCarts(101L,page,pageSize);
    }

    @PostMapping("carts")
    /**
     * TODO 解析Token获取customerId
     */
    public Mono newCart(@RequestBody NewCartVo newCartVo){
        return cartService.newCarts(101L,newCartVo);
    }

    @DeleteMapping("carts")
    /**
     * TODO 解析Token获取customerId
     */
    public Mono deleteAllCart(){
        return cartService.deleteAll(101L);
    }

    @PutMapping("carts/{id}")
    /**
     * TODO 解析Token获取customerId
     */
    public Mono modifiedCart(@PathVariable Long id,@RequestBody NewCartVo vo){
        return cartService.modifiedCart(101L,id,vo);
    }

    @DeleteMapping("carts/{id}")
    /**
     * TODO 解析Token获取customerId
     */
    public Mono deleteCart(@PathVariable Long id){
        return cartService.deleteCart(101L,id);
    }
}
