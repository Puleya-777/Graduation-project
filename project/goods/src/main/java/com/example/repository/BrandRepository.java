package com.example.repository;

import com.example.model.po.BrandPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BrandRepository extends ReactiveCrudRepository<BrandPo,Long> {
    Mono<Integer> deleteBrandPoById(Long id);
}
