package com.example.repository;

import com.example.model.po.CouponSkuPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CouponSkuRepository extends ReactiveCrudRepository<CouponSkuPo,Long> {

    Flux<CouponSkuPo> findAllByActivityId(Long activityId);

}
