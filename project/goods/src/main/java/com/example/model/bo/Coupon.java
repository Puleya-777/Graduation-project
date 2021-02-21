package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.CouponPo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Coupon implements VoObject {

    Long id;

    String couponSn;

    String name;

    Long customerId;

    Long  activityId;

    LocalDateTime beginTime;

    LocalDateTime endTime;

    Integer state;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public Coupon(CouponPo couponPo){
        id=couponPo.getId();
        couponSn=couponPo.getCouponSn();
        name=couponPo.getName();
        customerId=couponPo.getCustomerId();
        activityId=couponPo.getActivityId();
        beginTime=couponPo.getBeginTime();
        endTime=couponPo.getEndTime();
        state=couponPo.getState();
        gmtCreate=couponPo.getGmtCreate();
        gmtModified=couponPo.getGmtModified();
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
