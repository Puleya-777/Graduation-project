package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.CouponActivityPo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponActivityDetail implements VoObject {

    Long id;

    String name;

    Integer state;

    Shop shop;

    Integer quantity;

    Integer quantityType;

    Integer validTerm;

    String imageUrl;

    String beginTime;

    String endTime;

    String couponTime;

    String strategy;

    User createdBy;

    User ModiBy;

    String gmtCreate;

    String gmtModified;

    public CouponActivityDetail(CouponActivityPo couponActivityPo){
        id=couponActivityPo.getId();
        name=couponActivityPo.getName();
        state=couponActivityPo.getState();
        shop=new Shop();
        shop.setId(couponActivityPo.getShopId());
        quantity=couponActivityPo.getQuantity();
        quantityType=couponActivityPo.getQuantitiyType();
        validTerm=couponActivityPo.getValidTerm();
        imageUrl=couponActivityPo.getImageUrl();
        beginTime=couponActivityPo.getBeginTime().toString();
        endTime=couponActivityPo.getEndTime().toString();
        if(couponTime!=null)
            couponTime=couponActivityPo.getCouponTime().toString();
        strategy=couponActivityPo.getStrategy();
        createdBy=new User();
        createdBy.setId(couponActivityPo.getCreatedBy());
        ModiBy=new User();
        ModiBy.setId(couponActivityPo.getModiBy());
        gmtCreate=couponActivityPo.getGmtCreate().toString();
        if(couponActivityPo.getGmtModified()!=null)
            gmtModified=couponActivityPo.getGmtModified().toString();
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
