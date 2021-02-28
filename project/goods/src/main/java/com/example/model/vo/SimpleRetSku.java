package com.example.model.vo;

import com.example.model.VoObject;
import com.example.model.po.SkuPo;
import lombok.Data;

@Data
public class SimpleRetSku implements VoObject{

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

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
