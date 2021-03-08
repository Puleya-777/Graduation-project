package com.example.repository;

import com.example.model.bo.CouponSpu;
import com.example.model.po.CouponSpuPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CouponSpuRepository extends ReactiveCrudRepository<CouponSpuPo,Long> {

    Mono<CouponSpuPo> findByActivityId(Long activityId);

}
