package com.order.repository;

import com.order.model.po.FreightModelPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FreightModelRepository extends ReactiveCrudRepository<FreightModelPo,Long> {
    Flux<FreightModelPo> findByShopIdAndName(Long id, String name);
    Flux<FreightModelPo> findByName(String name);
    Flux<FreightModelPo> findByShopIdAndType(Long id,Byte type);
    Flux<FreightModelPo> findByShopIdAndDefaultModel(Long shopId,Byte defaultModel);
    Flux<FreightModelPo> findByShopId(Long id);
    Mono<Integer> deleteFreightModelById(Long id);
}
