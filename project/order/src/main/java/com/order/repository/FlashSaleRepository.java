package com.order.repository;

import com.order.model.po.FlashSalePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FlashSaleRepository extends ReactiveCrudRepository<FlashSalePo,Long> {
    Flux<FlashSalePo> findByTimeSegId(Long timeSegId);
}
