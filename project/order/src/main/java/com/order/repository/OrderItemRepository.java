package com.order.repository;

import com.order.model.po.OrderItemPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItemPo,Long> {
    Flux<OrderItemPo> findByOrderId(Long orderId);
}
