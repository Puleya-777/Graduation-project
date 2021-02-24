package com.example.repository;

import com.example.model.po.CouponActivityPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CouponActivityRepository extends ReactiveCrudRepository<CouponActivityPo,Long> {

    Flux<CouponActivityPo> findAllByShopId(Long shopId);

    Mono<CouponActivityPo> deleteCouponActivityPoById(Long id);

}
