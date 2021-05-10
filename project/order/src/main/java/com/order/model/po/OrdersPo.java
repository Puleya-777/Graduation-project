package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("orders")
public class OrdersPo {

    @Id
    private Long id;//主键

    private Long customerId;//购买者Id

    private Long shopId;//店铺Id

    private String orderSn;//订单号

    private Long pid;//父订单Id

    private String consignee;//收货人

    private Long regionId;//地区Id

    private String address;//收获详细地址

    private String mobile;//联系电话

    private String message;//附言

    private Integer orderType;//订单类型 0普通 1团购 2预售

    private Long freightPrice;//运费

    private Long couponId;//使用的优惠券Id

    private Long couponActivityId;//  优惠活动Id？

    private Long discountPrice;//优惠券/优惠活动折扣数额

    private Long originPrice;//订单商品优惠前总额

    private Long presaleId;//预售活动Id

    private Long grouponDiscount;//  团购优惠？

    private Integer rebateNum;//返给分享者的点数

    private LocalDateTime confirmTime;//发货时间

    private String shipmentSn;//快递单号

    private Integer state;//订单主状态

    private Integer substate;//订单子状态（用于团购和预售）

    private Byte beDeleted;//买家逻辑删除

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long grouponId;//团购活动Id

}