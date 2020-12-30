package com.example.model.vo;

import com.example.model.VoObject;
import com.example.model.po.SkuPo;

public class SimpleRetSku {

    Long id;

    String name;

    String skuSn;

    String imageUrl;

    Long inventory;

    Float originalPrice;

    Float price;

    Boolean disable;

    public SimpleRetSku(SkuPo skuPo){
        id=skuPo.getId();
        name=skuPo.getName();
        skuSn=skuPo.getSkuSn();
        imageUrl=skuPo.getImageUrl();
        inventory=skuPo.getInventory();
        originalPrice=skuPo.getOriginalPrice();
        price=originalPrice;
        disable=skuPo.getDisabled();
    }

}
