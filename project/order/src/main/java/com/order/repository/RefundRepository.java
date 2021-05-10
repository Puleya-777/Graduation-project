package com.order.repository;

import com.order.model.po.RefundPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RefundRepository extends ReactiveCrudRepository<RefundPo,Long> {
    Flux<RefundPo> findByOrderId(Long orderId);
    Flux<RefundPo> findByAftersaleId(Long aftersaleId);
}
