package com.order.repository;

import com.order.model.po.GoodsSkuPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface GoodsSkuRepository extends ReactiveCrudRepository<GoodsSkuPo,Long> {
}
