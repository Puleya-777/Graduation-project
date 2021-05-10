package com.order.model.vo;

import com.order.model.bo.FreightModelChangeBo;
import lombok.Data;


@Data
public class FreightModelChangeVo {

    private String name;

    private Integer unit;


    public FreightModelChangeBo createFreightModelBo()
    {
        FreightModelChangeBo freightModelChangeBo = new FreightModelChangeBo();
        freightModelChangeBo.setUnit(this.unit);
        freightModelChangeBo.setName(this.name);

        return freightModelChangeBo;
    }


}
