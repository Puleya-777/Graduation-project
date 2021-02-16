package com.example.dao;

import com.example.repository.CategoryRepository;
import com.example.util.ReturnObject;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CategoryDao {

    @Resource
    CategoryRepository categoryRepository;

    public Mono<List> queryCategoryRelation(Long id) {
        return categoryRepository.findAllByPid(id).collect(Collectors.toList());
    }
}
