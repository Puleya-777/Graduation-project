package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.BrandPo;

public class Brand implements VoObject {

    Long id;

    String name;

    String imageUrl;

    String detail;

    String gmtCreate;

    String gmtModified;

    public Brand(BrandPo brandPo){
        id=brandPo.getId();
        name=brandPo.getName();
        imageUrl=brandPo.getName();
        detail=brandPo.getDetail();
        gmtCreate=brandPo.getGmtCreate().toString();
        gmtModified=brandPo.getGmtModified().toString();
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
