package com.example.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("coupon_spu")
public class CouponSpuPo {

    @Id
    Long id;

    Long activityId;

    Long spuId;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

}
