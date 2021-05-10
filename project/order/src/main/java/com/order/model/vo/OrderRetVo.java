package com.order.model.vo;

import com.order.model.bo.Orders;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderRetVo implements com.example.model.VoObject, Serializable {
    private Long id;
    private Long customerId;
    private Long shopId;
    private Long pid;
    private Integer orderType;
    private Integer state;
    private Integer subState;
    private LocalDateTime gmtCreate;
    private Long originPrice;
    private Long discountPrice;
    private Long freightPrice;
    private Long grouponId;
    private Long presaleId;
    private String shipmentSn;




    public OrderRetVo(Orders orders) {
        this.id = orders.getId();
        this.pid=orders.getPid();
        this.customerId = orders.getCustomerId();
        this.shopId = orders.getShopId();
        this.orderType=orders.getOrderType();
        this.freightPrice=orders.getFreightPrice();
        this.discountPrice=orders.getDiscountPrice();
        this.originPrice=orders.getOriginPrice();
        this.shipmentSn=orders.getShipmentSn();
        this.state=orders.getState();
        this.gmtCreate = orders.getGmtCreated();
        this.presaleId=orders.getPresaleId();
        this.subState=orders.getSubstate();
        this.grouponId=orders.getGrouponId();
    }


    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
