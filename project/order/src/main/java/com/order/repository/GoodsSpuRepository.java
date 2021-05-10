package com.order.repository;

import com.order.model.po.GoodsSpuPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface GoodsSpuRepository extends ReactiveCrudRepository<GoodsSpuPo,Long> {
}
