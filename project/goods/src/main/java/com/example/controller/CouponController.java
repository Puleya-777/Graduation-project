package com.example.controller;

import com.example.annotation.LoginUser;
import com.example.model.vo.CouponActivityVo;
import com.example.service.CouponService;
import com.example.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;

@RestController
public class CouponController {

    @Autowired
    CouponService couponService;

    @GetMapping("/coupons/states")
    public Mono<Object> getCouponState(){
        return couponService.getCouponState().map(Common::getRetObject);
    }

    @PostMapping("/shops/{shopId}/couponactivities")
    public Mono<Object> addCouponActivity(@LoginUser Long userId, @PathVariable Long shopId,
                                          @RequestBody CouponActivityVo couponActivityVo){
        return couponService.addCouponActivity(userId,shopId, couponActivityVo).map(Common::getRetObject);
    }

    @PostMapping("/shops/{shopId}/couponactivities/{id}")
    public Mono<Object> upCouponActivityPicture(@LoginUser Long userId, @PathVariable Long shopId,
                                                @PathVariable Long id, File img){
        return null;
    }

    @GetMapping("/couponactivities")
    public Mono<Object> showOwncouponactivities(@RequestParam Long shopId,@RequestParam Integer timeline,
                                                @RequestParam Integer page,@RequestParam Integer pageSize){
        return couponService.showOwncouponactivities(shopId,timeline,page,pageSize).map(Common::getRetObject);
    }

    @GetMapping("/shops/{id}/couponactivities/invalid")
    public Mono<Object> showOwnInvalidcouponactivities(@LoginUser Long userId,@PathVariable Long id,
                                                       @RequestParam Integer page,@RequestParam Integer pageSize){
        return couponService.showOwnInvalidcouponactivities(id).map(Common::getRetObject);
    }

    @GetMapping("/couponactivities/{id}/skus")
    public Mono<Object> getCouponSku(@PathVariable Long id,@RequestParam Integer page,
                                     @RequestParam Integer pageSize){
        return couponService.getCouponSku(id,page,pageSize).map(Common::getRetObject);
    }

    @GetMapping("/shops/{shopId}/couponactivities/{id}")
    public Mono<Object> getCouponActivityDetails(@LoginUser Long userId,@PathVariable Long shopId,
                                                 @PathVariable Long id){
        return couponService.getCouponActivityDetails(shopId,id).map(Common::getRetObject);
    }

    @PutMapping("/shops/{shopId}/couponactivities/{id}")
    public Mono<Object> modifyActivity(@LoginUser Long userId,@PathVariable Long shopId,
                                       @PathVariable Long id,@RequestBody CouponActivityVo couponActivityVo){
        return couponService.modifyActivity(userId,shopId,id,couponActivityVo).map(Common::getRetObject);
    }

    @DeleteMapping("/shops/{shopId}/couponactivities/{id}")
    public Mono<Object> deleteActivity(@LoginUser Long userId,@PathVariable Long shopId,
                                       @PathVariable Long id){
        return couponService.deleteActivity(id).map(Common::getRetObject);
    }

    @PostMapping("/shops/{shopId}/couponactivities/{id}/skus")
    public Mono<Object> addRangeOfActivity(@LoginUser Long userId, @PathVariable Long shopId,
                                           @PathVariable Long id, @RequestBody List<Long> skus){
        return couponService.addRangeOfActivity(shopId,id,skus).map(Common::getRetObject);
    }

    @DeleteMapping("/shops/{shopId}/couponskus/{id}")
    public Mono<Object> deleteActivityRange(@LoginUser Long userId,@PathVariable Long shopId,
                                            @PathVariable Long id){
        return couponService.deleteActivityRange(id).map(Common::getRetObject);
    }

    @GetMapping("/coupons")
    public Mono<Object> showCoupons(@LoginUser Long userId,@RequestParam Integer state,
                                    @RequestParam Integer page,@RequestParam Integer pageSize){
        return couponService.showCoupons(userId,state,page,pageSize).map(Common::getRetObject);
    }

    @PostMapping("/couponactivities/{id}/usercoupons")
    public Mono<Object> customerAddCoupon(@LoginUser Long userId,@RequestParam Integer id){
        return null;
    }

    @PutMapping("/shops/{shopId}/couponactivities/{id}/onshelves")
    public Mono<Object> onShelvesCouponActivity(@LoginUser Long userId,@PathVariable Long shopId,
                                                @PathVariable Long id){
        return couponService.changeStateOfCouponActivity(id,1).map(Common::getRetObject);
    }

    @PutMapping("/shops/{shopId}/couponactivities/{id}/offshelves")
    public Mono<Object> offShelvesCouponActivity(@LoginUser Long userId,@PathVariable Long shopId,
                                                @PathVariable Long id){
        return couponService.changeStateOfCouponActivity(id,1).map(Common::getRetObject);
    }
}
