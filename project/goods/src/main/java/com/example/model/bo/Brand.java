package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.BrandPo;

public class Brand implements VoObject {

    Long id;

    String name;

    String imageUrl;

    public Brand(BrandPo brandPo){
        id=brandPo.getId();
        name=brandPo.getName();
        imageUrl=brandPo.getName();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
