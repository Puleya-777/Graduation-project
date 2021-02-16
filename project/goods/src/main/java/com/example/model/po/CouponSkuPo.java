package com.example.model.po;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("coupon_sku")
public class CouponSkuPo {

    Long id;

    Long activityId;

    Long skuId;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

}
