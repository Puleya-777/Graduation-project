package com.example.model.bo;

import com.example.model.po.SpuPo;
import com.example.model.vo.SimpleRetSku;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Spu {

    Long id;

    String name;

    Brand brand;

    Category category;

    Shop shop;

    Freight freight;

    String goodsSn;

    String detail;

    String imageUrl;

//    Spec spec;

    List<SimpleRetSku> skuList;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    Boolean disable;

    public Spu(SpuPo spuPo, Brand brand, Category category, Shop shop){
        id=spuPo.getId();
        name=spuPo.getName();
        this.brand=brand;
        this.category=category;
        this.shop=shop;
        goodsSn=spuPo.getGoodsSn();
        detail=spuPo.getDetail();
        imageUrl=spuPo.getImageUrl();
//        spec=spuPo.getSpec();

        gmtCreate=spuPo.getGmtCreate();
        gmtModified=spuPo.getGmtModified();
        disable=spuPo.getDisabled();
    }

}
