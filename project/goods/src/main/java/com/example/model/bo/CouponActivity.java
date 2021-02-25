package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.CouponActivityPo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CouponActivity implements VoObject {

    Long id;

    String name;

    String imageUrl;

    String beginTime;

    String endTime;

    Integer quantity;

    String couponTime;

    public CouponActivity(CouponActivityPo couponActivityPo){
        id=couponActivityPo.getId();
        name=couponActivityPo.getName();
        imageUrl=couponActivityPo.getImageUrl();
        if(couponActivityPo.getBeginTime()!=null)
            beginTime=couponActivityPo.getBeginTime().toString();
        if(couponActivityPo.getEndTime()!=null)
            endTime=couponActivityPo.getEndTime().toString();
        quantity=couponActivityPo.getQuantity();
        if(couponActivityPo.getCouponTime()!=null)
            couponTime=couponActivityPo.getCouponTime().toString();
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
