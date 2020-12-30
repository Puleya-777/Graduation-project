package com.example.model.po;

import com.example.model.vo.SkuVo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("goods_sku")
@Data
@NoArgsConstructor
public class SkuPo {

    @Id
    Long id;

    Long goodsSpuId;

    String skuSn;

    String name;

    Float originalPrice;

    String configuration;

    Float weight;

    String imageUrl;

    Integer state;

    Long inventory;

    String detail;

    Boolean disabled;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public SkuPo(SkuVo skuVo){
        skuSn=skuVo.getSn();
        name=skuVo.getName();
        originalPrice=skuVo.getOriginalPrice();
        configuration=skuVo.getConfiguration();
        weight=skuVo.getWeight();
        imageUrl=skuVo.getImageUrl();

        state=0;
        inventory=skuVo.getInventory();
        detail=skuVo.getDetail();

        disabled=true;

    }

}
