package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("payment")
public class PaymentPo {
    @Id
    private Long id;//主键

    private Long amount;//付款金额

    private Long actualAmount;//实际付款价格

    private String paymentPattern;//支付方式

    private LocalDateTime payTime;//支付时间

    private String paySn;//支付编号

    private LocalDateTime beginTime;//开始支付的时间

    private LocalDateTime endTime;//终止支付的时间

    private Long orderId;//对应的订单的ID

    private Byte state;//支付状态

    private Long aftersaleId;//对应售后单的ID

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;


}