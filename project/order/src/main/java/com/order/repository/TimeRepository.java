package com.order.repository;

import com.order.model.po.TimeSegmentPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TimeRepository extends ReactiveCrudRepository<TimeSegmentPo,Long> {
    Flux<TimeSegmentPo> findByType(Byte type);
    Mono<Integer> deleteByIdAndType(Long id,Byte type);
}
