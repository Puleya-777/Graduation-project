package com.order.model.vo;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderItemsCreateVo {

    @NotNull
    private Long skuId;

    private Integer quantity;

    private Long couponActId;

    public Long getGoodsSkuId() {
        return skuId;
    }

    public void setGoodsSkuId(Long goodsSkuId) {
        this.skuId = goodsSkuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getCouponActivityId() {
        return couponActId;
    }

    public void setCouponActivityId(Long couponActivityId) {
        this.couponActId = couponActivityId;
    }
}