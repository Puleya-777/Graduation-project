package com.order.model.vo;

import com.order.model.bo.WeightFreightModelChangeBo;
import lombok.Data;


@Data

public class WeightFreightModelChangeVo {

    private Long firstWeight;

    private Long firstWeightFreight;

    private Long tenPrice;

    private Long fiftyPrice;

    private Long hundredPrice;

    private Long trihunPrice;

    private Long abovePrice;

    private Long regionId;

    public WeightFreightModelChangeBo createWeightFreightModelBo()
    {
        WeightFreightModelChangeBo weightFreightModelChangeBo = new WeightFreightModelChangeBo();
        weightFreightModelChangeBo.setFirstWeight(this.firstWeight);
        weightFreightModelChangeBo.setFirstWeightFreight(this.firstWeightFreight);
        weightFreightModelChangeBo.setTenPrice(this.tenPrice);
        weightFreightModelChangeBo.setFiftyPrice(this.fiftyPrice);
        weightFreightModelChangeBo.setHundredPrice(this.hundredPrice);
        weightFreightModelChangeBo.setTrihunPrice(this.trihunPrice);
        weightFreightModelChangeBo.setAbovePrice(this.abovePrice);
        weightFreightModelChangeBo.setRegionId(this.regionId);
        System.out.println("Vo "+getFirstWeightFreight()+"Bo "+weightFreightModelChangeBo.getFirstWeightFreight());
        return weightFreightModelChangeBo;
    }
}
