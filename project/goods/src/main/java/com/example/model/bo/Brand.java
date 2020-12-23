package com.example.model.bo;

import com.example.model.po.BrandPo;

public class Brand {

    Long id;

    String name;

    String imageUrl;

    public Brand(BrandPo brandPo){
        id=brandPo.getId();
        name=brandPo.getName();
        imageUrl=brandPo.getName();
    }

}
