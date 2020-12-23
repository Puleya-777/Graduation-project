package com.example.repository;

import com.example.model.po.BrandPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BrandRepository extends ReactiveCrudRepository<BrandPo,Long> {
}
