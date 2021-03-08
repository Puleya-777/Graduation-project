package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.CouponSpuPo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CouponSpu implements VoObject {

    Long id;

    Long activityId;

    Long spuId;

    String gmtCreate;

    String gmtModified;

    public CouponSpu(CouponSpuPo couponSpuPo){
        id=couponSpuPo.getId();
        activityId=couponSpuPo.getActivityId();
        spuId=couponSpuPo.getSpuId();
        if(couponSpuPo.getGmtCreate()!=null)
            gmtCreate=couponSpuPo.getGmtCreate().toString();
        if(couponSpuPo.getGmtModified()!=null){
            gmtModified=couponSpuPo.getGmtModified().toString();
        }
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
