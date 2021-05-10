package com.order.repository;

import com.order.model.po.PresaleActivityPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PresaleRepository extends ReactiveCrudRepository<PresaleActivityPo,Long> {
    Flux<PresaleActivityPo> findByStateAndShopIdAndGoodsSkuId(Byte state, Long shopId, Long goodsSKUId);
}
