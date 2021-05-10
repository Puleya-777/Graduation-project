package com.order.model.vo;

import lombok.Data;

@Data
public class PresaleActivityInVo {

    private Long shopid;
    private Long goodsSkuId;
    private Integer state;
    private Integer timeline;
    private Integer page;
    private Integer pageSize;

    public PresaleActivityInVo(Long shopid, Long goodsSkuId, Integer state, Integer timeline, Integer page, Integer pageSize){
        this.shopid=shopid;
        this.goodsSkuId=goodsSkuId;
        this.state=state;
        this.timeline=timeline;
        this.page=page;
        this.pageSize=pageSize;
    }
}
