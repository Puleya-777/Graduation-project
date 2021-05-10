package com.order.model.vo;

import com.order.model.bo.Payment;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 支付单视图
 *
 **/
@Data
public class PaymentVo {
    @NotNull
    private Long price;

    @NotBlank
    private String paymentPattern;

    /**
     * 通过vo构造bo
     * @return
     */
    public Payment createPayment(){
        Payment payment = new Payment();
        payment.setAmount(this.price);
        payment.setActualAmount(this.price);
        payment.setPaymentPattern(this.paymentPattern);


        return payment;
    }
}
