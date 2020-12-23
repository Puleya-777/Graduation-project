package com.example.repository;

import com.example.model.po.SkuPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SkuRepository extends ReactiveCrudRepository<SkuPo,Long> {
}
