package com.order.repository;

import com.order.model.po.ShopPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ShopRepository extends ReactiveCrudRepository<ShopPo,Long> {
}
