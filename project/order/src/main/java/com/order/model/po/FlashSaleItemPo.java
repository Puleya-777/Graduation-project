package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("flash_sale_item")
@Data
public class FlashSaleItemPo {

    @Id
    private Long id;//主键

    private Long saleId;//秒杀Id

    private Long goodsSkuId;//商品skuId

    private Long price;//秒杀价格

    private Integer quantity;//秒杀数量

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}