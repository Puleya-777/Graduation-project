package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.GrouponActivityPo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GrouponDetail implements VoObject {

    Long id;

    String name;

    GoodsSpu goodsSpu;

    Shop shop;

    String strategy;

    Integer state;

    LocalDateTime beginTime;

    LocalDateTime endTime;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public GrouponDetail(GrouponActivityPo grouponActivityPo){
        id=grouponActivityPo.getId();
        name=grouponActivityPo.getName();

        goodsSpu=new GoodsSpu();
        goodsSpu.setId(grouponActivityPo.getGoodsSpuId());

        shop=new Shop();
        shop.setId(grouponActivityPo.getShopId());

        strategy=grouponActivityPo.getStrategy();
        state=grouponActivityPo.getState();
        beginTime=grouponActivityPo.getBeginTime();
        endTime=grouponActivityPo.getEndTime();
        gmtCreate = grouponActivityPo.getGmtCreate();
        if(grouponActivityPo.getGmtModified()!=null)
            gmtModified = grouponActivityPo.getGmtModified();
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
