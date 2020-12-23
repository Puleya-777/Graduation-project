package com.example.model.vo;

import com.example.model.bo.Sku;
import lombok.Data;

@Data
public class SkuVo {

    //商品型号序号(序号由spuSpec中的id，按段组成)
    String sn;

    //商品型号名称
    String name;

    //该型号原价
    Float originalPrice;

    //配置参数JSON
    String configuration;

    //重量
    Float weight;

    //图片链接
    String imageUrl;

    //库存
    Long inventory;

    //该型号描述
    String detail;

    public SkuVo(Sku sku){
        sn=sku.getSkuSn();
        name= sku.getName();
        originalPrice=sku.getOriginalPrice();
        configuration=sku.getConfiguration();
        weight=sku.getWeight();
        imageUrl=sku.getImageUrl();
        inventory=sku.getInventory();
        detail=sku.getDetail();
    }

}
