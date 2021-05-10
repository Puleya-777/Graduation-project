package com.order.model.vo;

import com.order.model.bo.Orders;
import lombok.Data;

import java.util.List;

@Data
public class AftersaleOrderVo {
    private List<OrderItemVo> orderItemVoList;

    private String consignee;

    private Long regionId;

    private String address;

    private String mobile;

    private String message;
    /**
     * 构造函数
     */
    public Orders createOrder() {
        Orders orders = new Orders();
//        orders.setId(id);
//        orders.setCustomerId(customerId);
//        orders.setShopId(shopId);
//        orders.setOrderSn(orderSn);
//        orders.setPid(pid);
        orders.setConsignee(consignee);
        orders.setRegionId(regionId);
        orders.setAddress(address);
        orders.setMobile(mobile);
        orders.setMessage(message);
//        orders.setOrderType(orderType);
//        orders.setFreightPrice(freightPrice);
//        orders.setCouponId(couponId);
//        orders.setCouponActivityId(couponActivityId);
//        orders.setDiscountPrice(discountPrice);
//        orders.setOriginPrice(originPrice);
//        orders.setPresaleId(presaleId);
//        orders.setGrouponDiscount(grouponDiscount);
//        orders.setRebateNum(rebateNum);
//        orders.setConfirmTime(confirmTime);
//        orders.setShipmentSn(shipmentSn);
//        orders.setState(state);
//        orders.setSubstate(substate);
//        orders.setBeDeleted(beDeleted);
//        orders.setGmtCreated(gmtCreated);
//        orders.setGmtModified(gmtModified);
        return orders;
    }
}
