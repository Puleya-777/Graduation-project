package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("refund")
public class RefundPo {
    @Id
    private Long id;//主键

    private Long paymentId;//支付的ID

    private Long amount;//退款金额

    private Long orderId;//对应的订单ID

    private Long aftersaleId;//对应的售后单ID

    private Byte state;//支付状态

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}