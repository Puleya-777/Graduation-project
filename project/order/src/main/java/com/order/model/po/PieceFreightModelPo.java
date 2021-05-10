package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("piece_freight_model")
public class PieceFreightModelPo {
    @Id
    Long id;//主键

    Long freightModelId;//运输模式id

    Integer firstItems;//首件数

    Long firstItemsPrice ;//首费

    Integer additionalItems;//续件数

    Long additionalItemsPrice;//续费

    Long regionId;//抵达地区码

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public PieceFreightModelPo updatePo(PieceFreightModelPo po)
    {
        freightModelId=po.getFreightModelId();
        gmtCreate=po.gmtCreate;
        if(firstItems==null){firstItems=po.firstItems;}
        if(firstItemsPrice==null){firstItemsPrice=po.firstItemsPrice;}
        if(additionalItems==null){additionalItems=po.additionalItems;}
        if(additionalItemsPrice==null){additionalItemsPrice=po.additionalItemsPrice;}
        if(regionId==null){regionId=po.regionId;}
        return this;
    }
}
