package com.order.model.vo;

import com.order.model.bo.FreightModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FreightModelRetVo {
    private Long id;

    private Long shopId;

    private String name;

    private Byte defaultModel;

    private LocalDateTime gmtModified;

    private LocalDateTime gmtCreated;

    private Byte type;

    private Integer unit;



    /**
     * 用FreightModel对象建立Vo对象
     */
    public FreightModelRetVo(FreightModel freightModel) {
        this.id = freightModel.getId();
        this.name = freightModel.getName();
        //this.shopId = freightModel.getShopId();
        this.defaultModel = freightModel.getDefaultModel();
        this.type = freightModel.getType();
        this.gmtCreated = freightModel.getGmtCreate();
        this.gmtModified = freightModel.getGmtModified();
        this.unit=freightModel.getUnit();
    }
}
