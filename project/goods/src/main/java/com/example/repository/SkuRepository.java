package com.example.repository;

import com.example.model.po.SkuPo;
import io.swagger.models.auth.In;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SkuRepository extends ReactiveCrudRepository<SkuPo,Long> {

    Mono<SkuPo> findBySkuSn(String skuSn);

    Mono<Integer> deleteSkuPoById(Long SkuId);

    Flux<SkuPo> findAllByGoodsSpuId(Long spuId);

}
