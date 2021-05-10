package com.order.repository;

import com.order.model.po.WeightFreightModelPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WeightFreightModelRepository extends ReactiveCrudRepository<WeightFreightModelPo,Long> {
    Flux<WeightFreightModelPo> findByFreightModelId(Long freightModelId);
    Mono<WeightFreightModelPo> deleteByFreightModelId(Long freightModelId);
    Flux<WeightFreightModelPo> findByFreightModelIdAndRegionId(Long freightModelId,Long regionId);
    Mono<Integer> deleteWeightFreightModelById(Long id);
}
