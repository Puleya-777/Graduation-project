package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("goods_sku")
@Data
public class GoodsSkuPo {

    @Id
    private Long id;

    private Long goodsSpuId;

    private String skuSn;

    private String name;

    private Long originalPrice;

    private String configuration;

    private Long weight;

    private String imageUrl;

    private Integer inventory;

    private String detail;

    private Byte disabled;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Byte state;

}