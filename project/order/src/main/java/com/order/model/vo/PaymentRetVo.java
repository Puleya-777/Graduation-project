package com.order.model.vo;

import com.order.model.bo.Payment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentRetVo{

    private Long id;

    private Long amount;

    private Long actualAmount;

    private String paymentPattern;

    private LocalDateTime payTime;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Long orderId;

    private Long aftersaleId;

    private Byte state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public PaymentRetVo(Payment payment) {
        this.actualAmount=payment.getActualAmount();
        this.amount=payment.getAmount();
        this.beginTime=payment.getBeginTime();
        this.endTime=payment.getEndTime();
        this.gmtCreate=payment.getGmtCreate();
        this.gmtModified=payment.getGmtModified();
        this.id=payment.getId();
        this.orderId=payment.getOrderId();
        this.paymentPattern=payment.getPaymentPattern();
        this.payTime=payment.getPayTime();
        this.state=payment.getState();
        this.aftersaleId=payment.getAftersaleId();
    }


}
