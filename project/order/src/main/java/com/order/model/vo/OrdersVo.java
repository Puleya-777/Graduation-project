package com.order.model.vo;

import com.order.model.bo.Orders;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrdersVo {

    @NotNull
    private List<com.order.model.vo.OrderItemsCreateVo> orderItems;

    private String consignee;

    private Long regionId;

    private String address;

    private String mobile;

    private String message;

    private Long couponId;

    private Long presaleId;

    private Long grouponId;

    public Orders createOrdersBo()
    {
        Orders orders = new Orders();
        orders.setGrouponId(this.grouponId);
        orders.setPresaleId(this.presaleId);
        orders.setCouponId(this.couponId);
        orders.setConsignee(this.consignee);
        orders.setRegionId(this.regionId);
        orders.setAddress(this.address);
        orders.setMobile(this.mobile);
        orders.setMessage(this.message);
        return orders;
    }
}
