package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.CouponPo;
import lombok.Data;

@Data
public class Coupon implements VoObject {

    Long id;

    CouponActivity activity;

    String name;

    String couponSn;


    public Coupon(CouponPo couponPo){
        id=couponPo.getId();
        couponSn=couponPo.getCouponSn();
        name=couponPo.getName();
        activity=new CouponActivity();
        activity.setId(couponPo.getId());
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
