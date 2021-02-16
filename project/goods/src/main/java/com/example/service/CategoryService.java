package com.example.service;

import com.example.model.po.CategoryPo;
import com.example.model.vo.CategoryInfoVo;
import com.example.repository.CategoryRepository;
import com.example.util.ReturnObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class CategoryService {

    @Resource
    CategoryRepository categoryRepository;


    public Mono<ReturnObject> addGoodsCategory(Long id, CategoryInfoVo categoryDetail) {
        CategoryPo categoryPo=new CategoryPo();
        categoryPo.setPid(id);
        categoryPo.setName(categoryDetail.getName());
        return categoryRepository.save(categoryPo).map(ReturnObject::new);
    }

    public Mono<ReturnObject> deleteCategory(Long id) {
        return categoryRepository.deleteCategoryPoById(id).map(ReturnObject::new);
    }
}
