package com.example.repository;

import com.example.model.po.CategoryPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveCrudRepository<CategoryPo,Long> {

    Flux<CategoryPo> findAllByPid(Long pid);

    Mono<Integer> deleteCategoryPoById(Long id);

}
