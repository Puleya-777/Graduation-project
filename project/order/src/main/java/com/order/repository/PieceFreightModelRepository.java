package com.order.repository;

import com.order.model.po.PieceFreightModelPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PieceFreightModelRepository extends ReactiveCrudRepository<PieceFreightModelPo,Long> {
    Flux<PieceFreightModelPo> findByFreightModelId(Long freightModelId);
    Mono<PieceFreightModelPo> deleteByFreightModelId(Long freightModelId);
    Flux<PieceFreightModelPo> findByFreightModelIdAndRegionId(Long FreightModelId,Long RegionId);
    Mono<Integer> deletePieceFreightModelById(Long id);
}
