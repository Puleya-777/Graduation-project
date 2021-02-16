package com.example.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FloatPriceVo {

    Integer activityPrice;

    LocalDateTime beginTime;

    LocalDateTime endTime;

    Integer quantity;

}
