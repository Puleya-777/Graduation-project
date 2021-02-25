package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.BrandPo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
        if(brandPo.getGmtCreate()!=null)
            gmtCreate=brandPo.getGmtCreate().toString();
        if(brandPo.getGmtModified()!=null)
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
