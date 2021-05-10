package com.order.model.bo;

import com.order.model.po.TimeSegmentPo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlashSaleWithTimeSegmentView {
    private final Long id;
    private final LocalDateTime flashDate;
    private final TimeSegmentView timeSeg;
    private final LocalDateTime gmtCreate;
    private final LocalDateTime gmtModified;

    @Data
    public static class TimeSegmentView {
        private final Long id;
        private final LocalDateTime beginTime;
        private final LocalDateTime endTime;
        private final LocalDateTime gmtCreate;
        private final LocalDateTime gmtModified;

        private TimeSegmentView(TimeSegmentPo timeSegmentPo) {
            this.id = timeSegmentPo.getId();
            this.beginTime = timeSegmentPo.getBeginTime();
            this.endTime = timeSegmentPo.getEndTime();
            this.gmtCreate = timeSegmentPo.getGmtCreate();
            this.gmtModified = timeSegmentPo.getGmtModified();
        }
    }

    public FlashSaleWithTimeSegmentView(FlashSale bo, TimeSegmentPo timeSegment) {
        this.id = bo.getId();
        this.flashDate = bo.getFlashDate();
        this.timeSeg = new TimeSegmentView(timeSegment);
        this.gmtCreate = bo.getGmtCreate();
        this.gmtModified = bo.getGmtModified();
    }
}
