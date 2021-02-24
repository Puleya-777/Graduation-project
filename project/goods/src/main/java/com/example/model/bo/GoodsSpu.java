package com.example.model.bo;

import com.example.model.po.SpuPo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoodsSpu {

    Long id;

    String name;

    String goodsSn;

    String imageUrl;

    Integer state;

    String gmtCreate;

    String gmtModified;

    Boolean disable;

    public GoodsSpu(SpuPo spuPo){
        id=spuPo.getId();
        name=spuPo.getName();
        goodsSn=spuPo.getGoodsSn();
        imageUrl=spuPo.getImageUrl();
        state=0;
        gmtCreate=spuPo.getGmtCreate().toString();
        gmtModified=spuPo.getGmtModified().toString();
        disable=spuPo.getDisabled();
    }

}
