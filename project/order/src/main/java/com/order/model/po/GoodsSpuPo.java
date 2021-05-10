package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("goods_spu")
@Data
public class GoodsSpuPo {

    @Id
    private Long id;

    private String name;

    private Long brandId;

    private Long categoryId;

    private Long freightId;

    private Long shopId;

    private String goodsSn;

    private String detail;

    private String imageUrl;

    private String spec;

    private Byte disabled;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}