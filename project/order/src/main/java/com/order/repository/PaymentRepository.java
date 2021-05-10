package com.order.repository;

import com.order.model.po.PaymentPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PaymentRepository extends ReactiveCrudRepository<PaymentPo,Long> {
    Flux<PaymentPo> findByOrderId(Long orderId);
    Flux<PaymentPo> findByAftersaleId(Long aftersaleId);
}
