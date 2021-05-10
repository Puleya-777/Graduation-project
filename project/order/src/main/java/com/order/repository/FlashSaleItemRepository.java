package com.order.repository;

import com.order.model.po.FlashSaleItemPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlashSaleItemRepository extends ReactiveCrudRepository<FlashSaleItemPo,Long> {
    Flux<FlashSaleItemPo> findBySaleId(Long saleId);
    Mono<Integer> deleteFlashSaleItemById(Long id);
}
