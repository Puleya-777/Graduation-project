package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("weight_freight_model")
public class WeightFreightModelPo {
    @Id
    Long id;//主键

    Long freightModelId;//运输模式id

    Long firstWeight;//首次重量

    Long firstWeightFreight;//首重价格

    Long tenPrice;//10kg以下每0.5kg价格

    Long fiftyPrice;//50kg以下每0.5kg价格

    Long hundredPrice;//100kg以下每0.5kg价格

    Long trihunPrice;//300kg以下每0.5kg价格

    Long abovePrice;//300kg以上每0.5kg价格

    Long regionId;//抵达地区码

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public WeightFreightModelPo updatePo(WeightFreightModelPo po)
    {
        if(firstWeight==null){firstWeight=po.firstWeight;}
        if(firstWeightFreight==null){firstWeightFreight=po.firstWeightFreight;}
        if(tenPrice==null){tenPrice=po.tenPrice;}
        if(fiftyPrice==null){fiftyPrice=po.fiftyPrice;}
        if(hundredPrice==null){hundredPrice=po.hundredPrice;}
        if(trihunPrice==null){trihunPrice=po.trihunPrice;}
        if(abovePrice==null){abovePrice=po.abovePrice;}
        if(regionId==null){regionId=po.regionId;}
        gmtCreate=po.gmtCreate;
        freightModelId=po.getFreightModelId();
        return this;
    }
}
