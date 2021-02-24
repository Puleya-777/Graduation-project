package com.example.model.po;

import com.example.model.vo.CouponActivityVo;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Table("coupon_activity")
@NoArgsConstructor
public class CouponActivityPo {

    @Id
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

        DateTimeFormatter df=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        beginTime=LocalDateTime.parse(couponActivityVo.getBeginTime(),df);
        endTime=LocalDateTime.parse(couponActivityVo.getEndTime(),df);
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

        DateTimeFormatter df=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if(!StringUtil.isNullOrEmpty(couponActivityVo.getBeginTime())){
            beginTime=LocalDateTime.parse(couponActivityVo.getBeginTime(),df);
        }
        if(!StringUtil.isNullOrEmpty(couponActivityVo.getEndTime())){
            endTime=LocalDateTime.parse(couponActivityVo.getEndTime(),df);
        }
        if(!StringUtil.isNullOrEmpty(couponActivityVo.getStrategy())){
            strategy=couponActivityVo.getStrategy();
        }

    }
}
