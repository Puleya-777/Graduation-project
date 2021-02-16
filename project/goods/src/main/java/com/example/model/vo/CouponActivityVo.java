package com.example.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponActivityVo {

    String name;

    Integer quantity;

    Integer quantityType;

    Integer validTerm;

    String beginTime;

    String endTime;

    String strategy;

}
