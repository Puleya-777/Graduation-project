package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.FloatPricePo;
import lombok.Data;

@Data
public class FloatPrice implements VoObject {

    Long id;

    Integer activityPrice;

    Integer quantity;

    String beginTime;

    String endTime;

    User createBy;

    User modifiedBy;

    Boolean valid;

    String gmtCreate;

    String gmtModified;

    public FloatPrice(FloatPricePo floatPricePo){
        id=floatPricePo.getId();
        activityPrice=floatPricePo.getActivityPrice();
        quantity=floatPricePo.getQuantity();
        beginTime=floatPricePo.getBeginTime().toString();
        endTime=floatPricePo.getEndTime().toString();
        createBy=new User();
        createBy.setId(floatPricePo.getCreatedBy());
        modifiedBy=new User();
        valid=floatPricePo.getValid();
        gmtCreate=floatPricePo.getGmtCreate().toString();
        gmtModified=floatPricePo.getGmtModified().toString();
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
