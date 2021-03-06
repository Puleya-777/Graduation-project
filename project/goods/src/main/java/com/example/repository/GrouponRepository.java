package com.example.repository;

import com.example.model.po.GrouponActivityPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GrouponRepository extends ReactiveCrudRepository<GrouponActivityPo,Long> {

    Flux<GrouponActivityPo> findAllByGoodsSpuIdAndShopId(Long goodsSpuId,Long shopId);
    Flux<GrouponActivityPo> findAllByGoodsSpuId(Long goodsSpuId);

    Mono<Integer> deleteGrouponActivityPoById(Long id);

    Flux<GrouponActivityPo> findAllByShopId(Long shopId);


}
