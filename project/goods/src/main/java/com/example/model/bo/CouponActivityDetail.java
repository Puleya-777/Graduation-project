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

    LocalDateTime beginTime;

    LocalDateTime endTime;

    LocalDateTime couponTime;

    Integer quantity;

    Integer quantityType;

    Integer validTerm;

    String imageUrl;

    String strategy;

    User createdBy;

    User modiBy;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public CouponActivityDetail(CouponActivityPo couponActivityPo,Shop shop,User createdBy,User modiBy){

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
