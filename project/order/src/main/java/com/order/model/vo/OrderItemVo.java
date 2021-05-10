package com.order.model.vo;
import lombok.Data;

@Data
public class OrderItemVo {

    private String skuId;


    private Integer count;

    public Integer getConut() {
        return  this.count;
    }

    public String getSkuId() {
        return this.skuId;
    }
}
