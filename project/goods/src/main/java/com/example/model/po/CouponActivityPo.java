package com.example.model.po;

import com.example.model.vo.CouponActivityVo;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("coupon_activity")
@NoArgsConstructor
public class CouponActivityPo {

    Long id;

    String name;

    LocalDateTime beginTime;

    LocalDateTime endTime;

    LocalDateTime couponTime;

    Integer state;

    Long shopId;

    Integer quantity;

    Integer validTerm;

    String imageUrl;

    String strategy;

    Long createdBy;

    Long modiBy;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    Integer quantitiyType;

    public CouponActivityPo(Long shopId,CouponActivityVo couponActivityVo){
        this.shopId=shopId;

        name=couponActivityVo.getName();
        quantity=couponActivityVo.getQuantity();
        quantitiyType=couponActivityVo.getQuantityType();
        validTerm=couponActivityVo.getValidTerm();
        beginTime=LocalDateTime.parse(couponActivityVo.getBeginTime());
        endTime=LocalDateTime.parse(couponActivityVo.getEndTime());
        strategy=couponActivityVo.getStrategy();
    }

    public void setByCouponActivityVo(CouponActivityVo couponActivityVo){
        if(!StringUtil.isNullOrEmpty(couponActivityVo.getName())){
            name=couponActivityVo.getName();
        }
        if(!StringUtil.isNullOrEmpty(String.valueOf(couponActivityVo.getQuantity()))){
            quantity=couponActivityVo.getQuantity();
        }
        if(!StringUtil.isNullOrEmpty(String.valueOf(couponActivityVo.getQuantityType()))){
            quantitiyType=couponActivityVo.getQuantityType();
        }
        if(!StringUtil.isNullOrEmpty(String.valueOf(couponActivityVo.getValidTerm()))){
            validTerm=couponActivityVo.getValidTerm();
        }
        if(!StringUtil.isNullOrEmpty(couponActivityVo.getBeginTime())){
            beginTime=LocalDateTime.parse(couponActivityVo.getBeginTime());
        }
        if(!StringUtil.isNullOrEmpty(couponActivityVo.getEndTime())){
            endTime=LocalDateTime.parse(couponActivityVo.getEndTime());
        }
        if(!StringUtil.isNullOrEmpty(couponActivityVo.getStrategy())){
            strategy=couponActivityVo.getStrategy();
        }

    }
}
