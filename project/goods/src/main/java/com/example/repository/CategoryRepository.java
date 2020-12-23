package com.example.repository;

import com.example.model.po.CategoryPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CategoryRepository extends ReactiveCrudRepository<CategoryPo,Long> {
}
