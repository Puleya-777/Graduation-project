package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.GrouponActivityPo;
import lombok.Data;

@Data
public class Groupon implements VoObject {

    Long id;

    String name;

    String beginTime;

    String endTime;

    public Groupon(GrouponActivityPo grouponActivityPo){
        id=grouponActivityPo.getId();
        name=grouponActivityPo.getName();
        beginTime=grouponActivityPo.getBeginTime().toString();
        endTime=grouponActivityPo.getEndTime().toString();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
