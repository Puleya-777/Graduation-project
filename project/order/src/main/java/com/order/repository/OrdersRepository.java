package com.order.repository;

import com.order.model.po.OrdersPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrdersRepository extends ReactiveCrudRepository<OrdersPo,Long> {
    Flux<OrdersPo> findByCustomerIdAndBeDeletedAndOrderSnAndState(Long customerId,Byte beDeleted,String orderSn,Integer state);
    Flux<OrdersPo> findByShopIdAndBeDeletedAndCustomerIdAndOrderSn(Long shopId,Byte beDeleted,Long customerId,String orderSn);
    Flux<OrdersPo> findByCustomerIdAndBeDeleted(Long customerId,Byte beDeleted);
    Flux<OrdersPo> findByShopIdAndBeDeleted(Long shopId,Byte beDeleted);
}
