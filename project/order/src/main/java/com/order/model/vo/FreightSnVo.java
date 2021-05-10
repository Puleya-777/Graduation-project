package com.order.model.vo;

import com.order.model.bo.Orders;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data

public class FreightSnVo {
    @NotBlank
    String freightSn;

    /**
     * 通过vo构造bo
     * @return
     */
    public Orders createOrder(){
        Orders orders = new Orders();
        orders.setShipmentSn(this.freightSn);

        return orders;
    }
}
