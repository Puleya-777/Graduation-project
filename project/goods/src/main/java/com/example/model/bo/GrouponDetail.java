package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.GrouponActivityPo;
import lombok.Data;

@Data
public class GrouponDetail implements VoObject {

    Long id;

    String name;

    GoodsSpu goodsSpu;

    Shop shop;

    String strategy;

    Integer state;

    String beginTime;

    String endTime;

    String gmtCreate;

    String gmtModified;

    public GrouponDetail(GrouponActivityPo grouponActivityPo){
        id=grouponActivityPo.getId();
        name=grouponActivityPo.getName();
        goodsSpu=new GoodsSpu();
        goodsSpu.setId(grouponActivityPo.getGoodsSpuId());
        shop=new Shop();
        shop.setId(grouponActivityPo.getShopId());
        strategy=grouponActivityPo.getStrategy();
        state=grouponActivityPo.getState();
        beginTime=grouponActivityPo.getBeginTime().toString();
        endTime=grouponActivityPo.getEndTime().toString();
        gmtCreate=grouponActivityPo.getGmtCreate().toString();
        gmtModified=grouponActivityPo.getGmtModified().toString();
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
