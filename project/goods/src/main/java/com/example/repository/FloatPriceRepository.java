package com.example.repository;

import com.example.model.po.FloatPricePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FloatPriceRepository extends ReactiveCrudRepository<FloatPricePo,Long> {
}
