package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("order_item")
public class OrderItemPo {

    @Id
    private Long id;//主键

    private Long orderId;//订单Id

    private Long goodsSkuId;//商品SkuId

    private Integer quantity;//数量

    private Long price;//商品单价

    private Long discount;//折让数目

    private String name;//商品名称

    private Long couponActivityId;//优惠活动Id

    private Long beShareId;//分享成功Id

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}