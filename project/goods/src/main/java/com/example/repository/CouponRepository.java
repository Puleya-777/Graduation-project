package com.example.repository;

import com.example.model.po.CouponPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CouponRepository extends ReactiveCrudRepository<CouponPo,Long> {

    Flux<CouponPo> findAllByCustomerIdAndState(Long customerId, Integer state);

}
