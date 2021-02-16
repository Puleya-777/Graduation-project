package com.example.model.po;

import com.example.model.vo.GrouponVo;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("groupon_activity")
public class GrouponActivityPo {

    Long id;

    String name;

    LocalDateTime beginTime;

    LocalDateTime endTime;

    Integer state;

    Long shopId;

    Long goodsSpuId;

    String strategy;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public GrouponActivityPo(Long goodsSpuId,Long shopId,GrouponVo grouponVo){
        this.goodsSpuId=goodsSpuId;
        this.shopId=shopId;

        strategy=grouponVo.getStrategy();
        beginTime=LocalDateTime.parse(grouponVo.getBeginTime());
        endTime=LocalDateTime.parse(grouponVo.getEndTime());
    }

    public void setByGrouponVo(GrouponVo grouponVo){
        if(!StringUtil.isNullOrEmpty(grouponVo.getStrategy())) {
            strategy = grouponVo.getStrategy();
        }
        if(!StringUtil.isNullOrEmpty(grouponVo.getBeginTime())) {
            beginTime=LocalDateTime.parse(grouponVo.getBeginTime());
        }
        if(!StringUtil.isNullOrEmpty(grouponVo.getEndTime())) {
            endTime=LocalDateTime.parse(grouponVo.getEndTime());
        }
    }
}
