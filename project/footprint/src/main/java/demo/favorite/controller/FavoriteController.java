package demo.favorite.controller;

import demo.favorite.service.FavoriteService;
import lombok.Getter;
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
public class FavoriteController {
    @Autowired
    FavoriteService favoriteService;

    @GetMapping("/favorites")
    /**
     * TODO 解析Token获取customerId
     */
    public Mono getFavorite( @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        return favoriteService.getFavorite(101L,page,pageSize);
    }

    @PostMapping("/favorites/goods/{skuId}")
    /**
     * TODO 解析Token获取customerId
     */
    public Mono newFavorite(@PathVariable Long skuId){
        return favoriteService.newFavorite(101L,skuId);
    }

    @DeleteMapping("/favorites/{id}")
    /**
     * TODO 解析Token获取customerId
     */
    public Mono deleteFavorite(@PathVariable Long id){
        return favoriteService.deleteFavorite(101L,id);
    }
}
