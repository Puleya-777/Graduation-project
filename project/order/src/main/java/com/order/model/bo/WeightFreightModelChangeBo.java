package com.order.model.bo;

import com.order.model.po.WeightFreightModelPo;
import lombok.Data;

@Data
public class WeightFreightModelChangeBo {

    /**
     * 代理对象
     */
    WeightFreightModelPo weightFreightModelPo;

    /**
     * 构造函数
     */
    public WeightFreightModelChangeBo()
    {
        this.weightFreightModelPo = new WeightFreightModelPo();
    }

    public WeightFreightModelChangeBo(WeightFreightModelPo weightFreightModelPo)
    {
        this.weightFreightModelPo = weightFreightModelPo;
    }

    public WeightFreightModelPo gotWeightFreightModelPo()
    {
        return this.weightFreightModelPo;
    }

    public Long getId() {

        return weightFreightModelPo.getId();
    }

    public void setId(Long id) {

        weightFreightModelPo.setId(id);
    }

    public void setFirstWeight(Long firstWeight) {

        weightFreightModelPo.setFirstWeight(firstWeight);
    }

    public Long getFirstWeightFreight() {

        return weightFreightModelPo.getFirstWeightFreight();
    }

    public void setFirstWeightFreight(Long firstWeightFreight) {

        weightFreightModelPo.setFirstWeightFreight(firstWeightFreight);
    }

    public Long getTenPrice() {

        return weightFreightModelPo.getTenPrice();
    }

    public void setTenPrice(Long tenPrice) {

        weightFreightModelPo.setTenPrice(tenPrice);
    }

    public Long getFiftyPrice() {

        return weightFreightModelPo.getFiftyPrice();
    }

    public void setFiftyPrice(Long fiftyPrice) {

        weightFreightModelPo.setFiftyPrice(fiftyPrice);
    }

    public Long getHundredPrice() {

        return weightFreightModelPo.getHundredPrice();
    }

    public void setHundredPrice(Long hundredPrice) {

        weightFreightModelPo.setHundredPrice(hundredPrice);
    }

    public Long getTrihunPrice() {

        return weightFreightModelPo.getTrihunPrice();
    }

    public void setTrihunPrice(Long trihunPrice) {

        weightFreightModelPo.setTrihunPrice(trihunPrice);
    }

    public Long getAbovePrice() {

        return weightFreightModelPo.getAbovePrice();
    }

    public void setAbovePrice(Long abovePrice) {

        weightFreightModelPo.setAbovePrice(abovePrice);
    }

    public Long getRegionId() {

        return weightFreightModelPo.getRegionId();
    }

    public void setRegionId(Long regionId) {

        weightFreightModelPo.setRegionId(regionId);
    }

}
