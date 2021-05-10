package com.order.model.vo;

import com.order.model.bo.PieceFreightModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 这个类是 “通过运费模板Id获得件数运费模板明细 “ API的返回对象。
 */

@Data
public class PieceFreightModelRetVo
{
    private Long id;

    private Long regionId;

    private Integer firstItems;

    private Long firstItemsPrice;

    private Integer additionalItems;

    private Long additionalItemsPrice;

    private LocalDateTime gmtModified;

    private LocalDateTime gmtCreated;

    /**
     * 用PieceFreightModel对象创建PieceFreightModelRetVo对象
     */
    public PieceFreightModelRetVo(PieceFreightModel bo)
    {
        this.id = bo.getId();
        this.regionId = bo.getRegionId();
        this.firstItems = bo.getFirstItems();
        this.firstItemsPrice = bo.getFirstItemsPrice();
        this.additionalItems = bo.getAdditionalItems();
        this.additionalItemsPrice = bo.getAdditionalItemsPrice();
        this.gmtModified = bo.getGmtModified();
        this.gmtCreated = bo.getGmtCreate();
    }
}
