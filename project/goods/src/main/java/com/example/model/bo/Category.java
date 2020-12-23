package com.example.model.bo;

import com.example.model.po.CategoryPo;
import lombok.Data;

@Data
public class Category {

    Long id;

    String name;

    public Category(CategoryPo categoryPo){
        id=categoryPo.getId();
        name=categoryPo.getName();
    }

}
