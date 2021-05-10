package com.order.model.vo;

import com.order.model.bo.Orders;
import lombok.Data;

/**
 * 管理员状态VO
 */
@Data
public class OrderStateVo {
    private Long Code;

    private String name;
    public OrderStateVo(Orders.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
