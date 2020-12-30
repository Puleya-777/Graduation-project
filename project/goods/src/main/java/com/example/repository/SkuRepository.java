package com.example.repository;

import com.example.model.po.SkuPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SkuRepository extends ReactiveCrudRepository<SkuPo,Long> {

    Mono<SkuPo> findBySkuSn(String skuSn);

}
