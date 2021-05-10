package com.order.model.vo;

import com.order.model.bo.Payment;

public class PaymentStateVo
{
    private Long Code;
    private String name;
    public PaymentStateVo(Payment.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }

    public Long getCode() {
        return Code;
    }

    public String getName() {
        return name;
    }

    public void setCode(Long code) {
        Code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}
