package demo.favorite.controller;

import com.example.annotation.Audit;
import com.example.annotation.LoginUser;
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
    @Audit
    public Mono getFavorite(@LoginUser Long userId, @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer pageSize){
        return favoriteService.getFavorite(userId,page,pageSize);
    }

    @PostMapping("/favorites/goods/{skuId}")
    @Audit
    public Mono newFavorite(@LoginUser Long userId,@PathVariable Long skuId){
        return favoriteService.newFavorite(userId,skuId);
    }

    @DeleteMapping("/favorites/{id}")
    @Audit
    public Mono deleteFavorite(@LoginUser Long userId,@PathVariable Long id){
        return favoriteService.deleteFavorite(userId,id);
    }
}
