package com.order.model.vo;

import com.order.model.bo.WeightFreightModel;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class WeightFreightModelRetVo
{
    private Long id;

    private Long firstWeight;

    private Long firstWeightFreight;

    private Long tenPrice;

    private Long fiftyPrice;

    private Long hundredPrice;

    private Long trihunPrice;

    private Long abovePrice;

    private Long regionId;

    private LocalDateTime gmtModified;

    private LocalDateTime gmtCreated;


    public WeightFreightModelRetVo(WeightFreightModel bo)
    {
        this.id = bo.getId();
        this.firstWeight = bo.getFirstWeight();
        this.firstWeightFreight = bo.getFirstWeightFreight();
        this.tenPrice = bo.getTenPrice();
        this.fiftyPrice = bo.getFiftyPrice();
        this.hundredPrice = bo.getHundredPrice();
        this.trihunPrice = bo.getTrihunPrice();
        this.abovePrice = bo.getAbovePrice();
        this.regionId = bo.getRegionId();
        this.gmtCreated = bo.getGmtCreate();
        this.gmtModified = bo.getGmtModified();
    }
}
