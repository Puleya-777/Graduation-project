package com.order.model.vo;

import com.order.model.bo.Time;
import lombok.Data;

import java.time.format.DateTimeFormatter;

/**
 * 时间返回Vo
 */
@Data
public class TimeRetVo {

    private Long id;

    private String beginTime;

    private String endTime;

    private String gmtCreate;

    private String gmtModified;

    /**
     * 通过bo构造
     */
    public TimeRetVo(Time bo) {
        this.setId(bo.getId());
        if (bo.getBeginTime() != null) {
            this.setBeginTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getBeginTime()));
        }
        if (bo.getEndTime() != null) {
            this.setEndTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getEndTime()));
        }
        if (bo.getGmtCreate() != null) {
            this.setGmtCreate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtCreate()));
        }
        if (bo.getGmtModified() != null) {
            this.setGmtModified(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtModified()));
        }
    }
}
