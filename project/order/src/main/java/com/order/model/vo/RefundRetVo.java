package com.order.model.vo;

import com.order.model.bo.Refund;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class RefundRetVo {

    private Long id;

    private Long paymentId;

    private Long amount;

    private Byte state;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    private Long orderId;

    private Long aftersaleId;

    public RefundRetVo(Refund refundBo)
    {
        this.aftersaleId = refundBo.getId();
        this.amount = refundBo.getAmount();
        this.gmtCreated = refundBo.getGmtCreate();
        this.gmtModified = refundBo.getGmtModified();
        this.id = refundBo.getId();
        this.orderId = refundBo.getOrderId();
        this.state = refundBo.getState();
        this.paymentId = refundBo.getPaymentId();
    }
}
