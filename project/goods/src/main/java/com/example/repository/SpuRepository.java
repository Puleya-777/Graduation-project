package com.example.repository;

import com.example.model.po.SpuPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SpuRepository extends ReactiveCrudRepository<SpuPo,Long> {
}
