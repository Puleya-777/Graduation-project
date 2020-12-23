package com.example.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("goods_sku")
@Data
public class SkuPo {

    @Id
    Long id;

    Long goodsSpuId;

    String SkuSn;

    String name;

    Float originalPrice;

    String configuration;

    Float weight;

    String imageUrl;

    Integer State;

    Long inventory;

    String detail;

    Boolean disabled;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

}
