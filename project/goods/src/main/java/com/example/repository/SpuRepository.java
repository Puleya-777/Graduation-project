package com.example.repository;

import com.example.model.po.SpuPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpuRepository extends ReactiveCrudRepository<SpuPo,Long> {

    Mono<SpuPo> deleteSpuPoById(Long id);

    Flux<SpuPo> findAllByShopId(Long shopId);

}
