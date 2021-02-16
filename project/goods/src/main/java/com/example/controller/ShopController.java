package com.example.controller;

import com.example.annotation.LoginUser;
import com.example.model.vo.AuditShopVo;
import com.example.model.vo.ShopVo;
import com.example.service.ShopService;
import com.example.util.Common;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class ShopController {

    @Autowired
    ShopService shopService;


    @GetMapping("/shops/states")
    public Mono<Object> getshopState(){
        return shopService.getshopState().map(Common::getRetObject);
    }

    @PostMapping("/shops")
    public Mono<Object> addShop(@LoginUser Long userId, @RequestBody ShopVo shopVo){
        return shopService.addShop(shopVo).map(Common::getRetObject);
    }

    @PutMapping("/shops/{id}")
    public Mono<Object> modifyShop(@LoginUser Long userId,@PathVariable Long id,
                                   @RequestBody ShopVo shopVo){
        return shopService.modifyShop(id,shopVo).map(Common::getRetObject);
    }

    @DeleteMapping("/shops/{id}")
    public Mono<Object> deleteShop(@LoginUser Long userId,@PathVariable Long id){
        return shopService.deleteShop(id);
    }

    @PutMapping("/shops/{shopId}/newshops/{id}/audit")
    public Mono<Object> auditShop(@LoginUser Long userId, @PathVariable Long shopId,
                                  @PathVariable Long id, @RequestBody AuditShopVo auditShopVo){
        return shopService.auditShop(shopId,id,auditShopVo);
    }

    @PutMapping("/shops/{id}/onshelves")
    public Mono<Object> onShelvesShop(@LoginUser Long userId,@PathVariable Long id){
        return shopService.onShelvesShop(id).map(Common::getRetObject);
    }

    @PutMapping("/shops/{id}/offshelves")
    public Mono<Object> offShelvesShop(@LoginUser Long userId,@PathVariable Long id){
        return shopService.offShelvesShop(id).map(Common::getRetObject);
    }
}
