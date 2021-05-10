package com.order.model.vo;


import com.order.model.bo.Orders;
import lombok.Data;

@Data
public class OrderSimpleVo {

    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    /**
     * 构造函数
     */
    public Orders createOrders() {
        Orders orders = new Orders();
        orders.setConsignee(this.consignee);
        orders.setRegionId(this.regionId);
        orders.setAddress(this.address);
        orders.setMobile(this.mobile);
        return orders;
    }

}
