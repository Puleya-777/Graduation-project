package com.example.model.po;

import com.example.model.vo.FloatPriceVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class FloatPricePo {

    Long id;

    Long goodsSkuId;

    Integer activityPrice;

    LocalDateTime beginTime;

    LocalDateTime endTime;

    Integer quantity;

    Long createdBy;

    Long invalidBy;

    Boolean valid;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public FloatPricePo(FloatPriceVo floatPriceVo,Long skuId,Long creatUserId){
        goodsSkuId=skuId;

        activityPrice=floatPriceVo.getActivityPrice();
        beginTime=floatPriceVo.getBeginTime();
        endTime=floatPriceVo.getEndTime();
        quantity=floatPriceVo.getQuantity();
        createdBy=creatUserId;

        invalidBy=createdBy;
        valid=true;
    }

}
