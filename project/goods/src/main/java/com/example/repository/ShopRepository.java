package com.example.repository;

import com.example.model.po.ShopPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ShopRepository extends ReactiveCrudRepository<ShopPo,Long> {
}
