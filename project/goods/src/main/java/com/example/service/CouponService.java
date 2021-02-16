package com.example.service;

import com.example.model.po.CouponActivityPo;
import com.example.model.po.CouponSkuPo;
import com.example.model.vo.CouponActivityVo;
import com.example.repository.CouponActivityRepository;
import com.example.repository.CouponRepository;
import com.example.repository.CouponSkuRepository;
import com.example.repository.SkuRepository;
import com.example.util.ReturnObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Resource
    CouponRepository couponRepository;
    @Resource
    CouponActivityRepository couponActivityRepository;
    @Resource
    CouponSkuRepository couponSkuRepository;
    @Resource
    SkuRepository skuRepository;

    public Mono<ReturnObject> getCouponState() {
        return couponRepository.findAll().map(couponPo -> couponPo.getState())
                .distinct().collect(Collectors.toList()).map(ReturnObject::new);
    }

    public Mono<ReturnObject> addCouponActivity(Long userId, Long shopId, CouponActivityVo couponActivityVo) {
        CouponActivityPo couponActivityPo=new CouponActivityPo(shopId,couponActivityVo);
        couponActivityPo.setCreatedBy(userId);
        couponActivityPo.setModiBy(userId);
        return couponActivityRepository.save(couponActivityPo).map(ReturnObject::new);
    }

    public Mono<ReturnObject> showOwncouponactivities(Long shopId, Integer timeline, Integer page, Integer pageSize) {
        return couponActivityRepository.findAllByShopId(shopId).collect(Collectors.toList())
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> showOwnInvalidcouponactivities(Long shopId) {
        return couponActivityRepository.findAllByShopId(shopId)
                .filter(couponActivityPo -> couponActivityPo.getState()==1)
                .collect(Collectors.toList())
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> getCouponSku(Long id, Integer page, Integer pageSize) {
        return couponSkuRepository.findAllByActivityId(id)
                .collect(Collectors.toList()).map(ReturnObject::new);
    }


    public Mono<ReturnObject> getCouponActivityDetails(Long shopId, Long id) {
        return couponActivityRepository.findById(id).map(ReturnObject::new);
    }

    public Mono<ReturnObject> modifyActivity(Long userId, Long shopId, Long id, CouponActivityVo couponActivityVo) {
        return couponActivityRepository.findById(id).flatMap(couponActivityPo -> {
            couponActivityPo.setByCouponActivityVo(couponActivityVo);
            couponActivityPo.setModiBy(userId);
            return couponActivityRepository.save(couponActivityPo);
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> deleteActivity(Long id) {
        return couponActivityRepository.deleteById(id).map(ReturnObject::new);
    }

    public Mono<ReturnObject> addRangeOfActivity(Long shopId, Long activityId, List<Long> skus) {
        return couponSkuRepository.saveAll(skus.stream().map(skuId->{
            CouponSkuPo couponSkuPo=new CouponSkuPo();
            couponSkuPo.setSkuId(skuId);
            couponSkuPo.setActivityId(activityId);
            return couponSkuPo;
        }).collect(Collectors.toList()))
                .collect(Collectors.toList()).map(ReturnObject::new);

    }

    public Mono<ReturnObject> deleteActivityRange(Long spuId) {
        return skuRepository.findAllByGoodsSpuId(spuId).map(skuPo -> {
            couponSkuRepository.deleteById(skuPo.getId());
            return skuPo;
        }).collect(Collectors.toList()).map(ReturnObject::new);

    }

    public Mono<ReturnObject> showCoupons(Long userId, Integer state, Integer page, Integer pageSize) {
        return couponRepository.findByCustomerIdAAndState(userId,state).map(ReturnObject::new);
    }

    public Mono<ReturnObject> changeStateOfCouponActivity(Long id, int state) {
        return couponRepository.findById(id).flatMap(couponPo -> {
            couponPo.setState(state);
            return couponRepository.save(couponPo);
        }).map(ReturnObject::new);
    }
}
