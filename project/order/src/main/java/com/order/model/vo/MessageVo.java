package com.order.model.vo;

import com.order.model.bo.Orders;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
* 订单留言修改视图
*
**/

@Data
public class MessageVo {

    @NotBlank
    String message;

    /**
     * 通过vo构造bo
     * @return
     */
    public Orders createOrder(){
        Orders orders = new Orders();
        orders.setMessage(this.message);

        return orders;
    }
}
