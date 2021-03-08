package com.example.controller;

import com.example.annotation.LoginUser;
import com.example.model.state.CouponState;
import com.example.model.state.CouponStateVo;
import com.example.model.state.SpuState;
import com.example.model.state.SpuStateVo;
import com.example.model.vo.CouponActivityVo;
import com.example.service.CouponService;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ResponseUtil;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CouponController {

    @Autowired
    CouponService couponService;

    @GetMapping("/coupons/states")
    public Mono<Object> getCouponState(){
        CouponState[] states= CouponState.class.getEnumConstants();
        List<CouponStateVo> CouponStateVos =new ArrayList<CouponStateVo>();
        for(int i=0;i<states.length;i++){
            CouponStateVos.add(new CouponStateVo(states[i]));
        }
        return Mono.just(ResponseUtil.ok(new ReturnObject<List>(CouponStateVos).getData()));
    }

    @PostMapping("/shops/{shopId}/couponactivities")
    public Mono<Object> addCouponActivity(@LoginUser Long userId, @PathVariable Long shopId,
                                          @RequestBody CouponActivityVo couponActivityVo){
        return couponService.addCouponActivity(userId,shopId, couponActivityVo).map(Common::getRetObject);
    }

    @PostMapping("/shops/{shopId}/couponactivities/{id}")
    public Mono<Object> upCouponActivityPicture(@LoginUser Long userId, @PathVariable Long shopId,
                                                @PathVariable Long id, @RequestParam("file") MultipartFile img){
        return couponService.upCouponActivityPicture(id,img);
    }

    @GetMapping("/couponactivities")
    public Mono<Object> showOwncouponactivities(@RequestParam(required = false) Long shopId,@RequestParam Integer timeline,
                                                @RequestParam(required = false,defaultValue = "1") Integer page,
                                                @RequestParam(required = false,defaultValue = "1") Integer pageSize){
        return couponService.showOwncouponactivities(shopId,timeline,page,pageSize).map(Common::getPageRetObject);
    }

    @GetMapping("/shops/{id}/couponactivities/invalid")
    public Mono<Object> showOwnInvalidcouponactivities(@LoginUser Long userId,@PathVariable Long id,
                                                       @RequestParam(required = false,defaultValue = "1") Integer page,
                                                       @RequestParam(required = false,defaultValue = "1") Integer pageSize){
        return couponService.showOwnInvalidcouponactivities(id,page,pageSize).map(Common::getPageRetObject);
    }

    /**
     *
     * @param id 活动id

     * @return
     */
    @GetMapping("/couponactivities/{id}/skus")
    public Mono<Object> getCouponSu(@PathVariable Long id){
        return couponService.getCouponSpu(id).map(Common::getRetObject);
    }

    @GetMapping("/shops/{shopId}/couponactivities/{id}")
    public Mono<Object> getCouponActivityDetails(@LoginUser Long userId,@PathVariable Long shopId,
                                                 @PathVariable Long id){
        return couponService.getCouponActivityDetails(shopId,id).map(Common::getRetObject);
    }

    @PutMapping("/shops/{shopId}/couponactivities/{id}")
    public Mono<Object> modifyActivity(@LoginUser Long userId,@PathVariable Long shopId,
                                       @PathVariable Long id,@RequestBody CouponActivityVo couponActivityVo){
        return couponService.modifyActivity(userId,shopId,id,couponActivityVo).map(ret->{
            if(ret.getCode()== ResponseCode.OK){
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.fail(ResponseCode.COUPONACT_STATENOTALLOW);
            }
        });
    }

    @DeleteMapping("/shops/{shopId}/couponactivities/{id}")
    public Mono<Object> deleteActivity(@LoginUser Long userId,@PathVariable Long shopId,
                                       @PathVariable Long id){
        return couponService.deleteActivity(id).map(returnObject -> ResponseUtil.ok());
    }

    /**
     *
     * @param userId
     * @param shopId
     * @param id    活动id
     * @param spuId
     * @return
     */
    @PostMapping("/shops/{shopId}/couponactivities/{id}/skus")
    public Mono<Object> addRangeOfActivity(@LoginUser Long userId, @PathVariable Long shopId,
                                           @PathVariable Long id, @RequestParam Long spuId){
        return couponService.addRangeOfActivity(shopId,id,spuId)
                .map(returnObject -> Common.getRetObject(returnObject));
    }

    /**
     *
     * @param userId
     * @param shopId
     * @param id   couponSpuId
     * @return
     */
    @DeleteMapping("/shops/{shopId}/couponskus/{id}")
    public Mono<Object> deleteActivityRange(@LoginUser Long userId,@PathVariable Long shopId,
                                            @PathVariable Long id){
        return couponService.deleteActivityRange(id).map(returnObject -> ResponseUtil.ok());
    }

    @GetMapping("/coupons")
    public Mono<Object> showCoupons(@LoginUser Long userId,@RequestParam Integer state,
                                    @RequestParam(required = false,defaultValue = "1") Integer page,@RequestParam(required = false,defaultValue = "1") Integer pageSize){
        return couponService.showCoupons(userId,state,page,pageSize).map(Common::getRetObject);
    }

    @PostMapping("/couponactivities/{id}/usercoupons")
    public Mono<Object> customerAddCoupon(@LoginUser Long userId,@RequestParam Long id){
        return couponService.customerAddCoupon(userId,id).map(Common::getRetObject);
    }

    @PutMapping("/shops/{shopId}/couponactivities/{id}/onshelves")
    public Mono<Object> onShelvesCouponActivity(@LoginUser Long userId,@PathVariable Long shopId,
                                                @PathVariable Long id){
        return couponService.changeStateOfCouponActivity(id,1).map(returnObject -> ResponseUtil.ok());
    }

    @PutMapping("/shops/{shopId}/couponactivities/{id}/offshelves")
    public Mono<Object> offShelvesCouponActivity(@LoginUser Long userId,@PathVariable Long shopId,
                                                @PathVariable Long id){
        return couponService.changeStateOfCouponActivity(id,1).map(ret->ResponseUtil.ok());
    }
}
