package demo.cart.controller;

import com.example.annotation.Audit;
import com.example.annotation.LoginUser;
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
    @Audit
    public Mono getCarts(@LoginUser Long userId,
                         @RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer pageSize){
        return cartService.getCarts(userId,page,pageSize);
    }

    @PostMapping("carts")
    @Audit
    public Mono newCart(@LoginUser Long userId,@RequestBody NewCartVo newCartVo){
        return cartService.newCarts(userId,newCartVo);
    }

    @DeleteMapping("carts")
    @Audit
    public Mono deleteAllCart(@LoginUser Long userId){
        return cartService.deleteAll(userId);
    }

    @PutMapping("carts/{id}")
    @Audit
    public Mono modifiedCart(@LoginUser Long userId,@PathVariable Long id,@RequestBody NewCartVo vo){
        return cartService.modifiedCart(userId,id,vo);
    }

    @DeleteMapping("carts/{id}")
    @Audit
    public Mono deleteCart(@LoginUser Long userId,@PathVariable Long id){
        return cartService.deleteCart(userId,id);
    }
}
