package com.order.model.vo;

import com.order.model.bo.PieceFreightModelChangeBo;
import lombok.Data;


@Data
public class PieceFreightModelChangeVo {

    private Integer firstItems;

    private Long firstItemsPrice;

    private Integer additionalItems;

    private Long additionalItemsPrice;

    private Long regionId;

    public PieceFreightModelChangeBo createPieceFreightModelChangeBo()
    {
        PieceFreightModelChangeBo pieceFreightModelChangeBo = new PieceFreightModelChangeBo();
        pieceFreightModelChangeBo.setAdditionalItems(this.additionalItems);
        pieceFreightModelChangeBo.setAdditionalItemsPrice(this.additionalItemsPrice);
        pieceFreightModelChangeBo.setFirstItems(this.firstItems);
        pieceFreightModelChangeBo.setFirstItemsPrice(this.firstItemsPrice);
        pieceFreightModelChangeBo.setRegionId(this.regionId);

        return pieceFreightModelChangeBo;
    }
}
