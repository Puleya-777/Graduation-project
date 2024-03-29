package com.example.model.po;

import com.example.model.vo.GrouponVo;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Table("groupon_activity")
@NoArgsConstructor
public class GrouponActivityPo {

    @Id
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
        state=0;

        this.goodsSpuId=goodsSpuId;
        this.shopId=shopId;

        strategy=grouponVo.getStrategy();

        DateTimeFormatter df=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        beginTime=LocalDateTime.parse(grouponVo.getBeginTime(),df);
        endTime=LocalDateTime.parse(grouponVo.getEndTime(),df);
    }

    public void setByGrouponVo(GrouponVo grouponVo){
        if(!StringUtil.isNullOrEmpty(grouponVo.getStrategy())) {
            strategy = grouponVo.getStrategy();
        }
        DateTimeFormatter df=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if(!StringUtil.isNullOrEmpty(grouponVo.getBeginTime())) {
            beginTime=LocalDateTime.parse(grouponVo.getBeginTime(),df);
        }
        if(!StringUtil.isNullOrEmpty(grouponVo.getEndTime())) {
            endTime=LocalDateTime.parse(grouponVo.getEndTime(),df);
        }
    }
}
