package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("flash_sale")
@Data
public class FlashSalePo {
    @Id
    private Long id;//主键

    private LocalDateTime flashDate;//秒杀日期

    private Long timeSegId;//时间段

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Integer state;//秒杀状态

}