package com.example.model.vo;

import com.example.model.VoObject;
import com.example.model.po.SkuPo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimpleRetSku implements VoObject{

    Long id;

    String name;

    Integer state;

    String skuSn;

    String imageUrl;

    Long inventory;

    Float originalPrice;

    Float price;

    Boolean disable;

    public SimpleRetSku(SkuPo skuPo){
        id=skuPo.getId();
        name=skuPo.getName();
        state=skuPo.getState();
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
