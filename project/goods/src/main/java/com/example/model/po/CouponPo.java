package com.example.model.po;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("coupon")
public class CouponPo {

    Long id;

    String couponSn;

    String name;

    Long customerId;

    Long  activityId;

    LocalDateTime beginTime;

    LocalDateTime endTime;

    Integer state;

}
