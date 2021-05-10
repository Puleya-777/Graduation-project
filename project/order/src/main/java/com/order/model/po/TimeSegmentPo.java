package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("time_segment")
public class TimeSegmentPo {

    @Id
    private Long id;//主键

    private LocalDateTime beginTime;//开始日期时间

    private LocalDateTime endTime;//结束日期时间

    private Byte type;//0广告时段 1秒杀时段

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}