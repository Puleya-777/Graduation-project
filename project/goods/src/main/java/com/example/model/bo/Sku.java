package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.SkuPo;
import com.example.model.vo.SkuVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Sku implements VoObject {

    Long id;

    String name;

    String skuSn;

    String detail;

    String imageUrl;

    Float originalPrice;

    Float price;

    Long inventory;

    Integer state;

    String configuration;

    Float weight;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    Spu spu;

    Boolean disable;

    Boolean shareable;

    public Sku(SkuPo skuPo, Spu spu){
        id=skuPo.getId();
        name=skuPo.getName();
        skuSn=skuPo.getSkuSn();
        detail=skuPo.getDetail();
        imageUrl=skuPo.getImageUrl();
        originalPrice=skuPo.getOriginalPrice();

        inventory=skuPo.getInventory();
        state=skuPo.getState();
        configuration=skuPo.getConfiguration();
        weight=skuPo.getWeight();

        this.spu=spu;

        gmtCreate=skuPo.getGmtCreate();
        gmtModified=skuPo.getGmtModified();
        disable=skuPo.getDisabled();
    }

    public Sku(SkuPo skuPo){
        id=skuPo.getId();
        name=skuPo.getName();
        skuSn=skuPo.getSkuSn();
        detail=skuPo.getDetail();
        imageUrl=skuPo.getImageUrl();
        originalPrice=skuPo.getOriginalPrice();

        inventory=skuPo.getInventory();
        state=skuPo.getState();
        configuration=skuPo.getConfiguration();
        weight=skuPo.getWeight();

        gmtCreate=skuPo.getGmtCreate();
        gmtModified=skuPo.getGmtModified();
        disable=skuPo.getDisabled();
    }

    @Override
    public SkuVo createVo() {
        return new SkuVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
