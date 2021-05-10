package com.order.model.vo;

import com.order.model.bo.FreightModel;
import lombok.Data;


@Data
public class FreightModelVo {
    private String name;

    private Byte type;

    private Integer unit;

    /**
     * 构造函数
     */
    public FreightModel createFreightModel() {
        FreightModel freightModel = new FreightModel();
        freightModel.setType(this.type);
        freightModel.setName(this.name);
        freightModel.setUnit(this.unit);
        return freightModel;
    }
}
