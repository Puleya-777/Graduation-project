package com.order.model.vo;

import com.order.model.bo.Shop;
import com.order.model.bo.ShopIdAndNameView;
import com.order.model.po.PresaleActivityPo;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class PresaleActivityCreateVo {

    private Long id;
    private String name;
    private String beginTime;
    private String payTime;
    private String endTime;
    private String state;
    private ShopIdAndNameView shop;
    private ReturnGoodsSkuVo goodsSku;
    private Integer quantity;
    private Long advancePayPrice;
    private Long restPayPrice;
    private String gmtCreate;
    private String gmtModified;

    public PresaleActivityCreateVo(Shop shop, ReturnGoodsSkuVo goodsSku, PresaleActivityPo po)
    {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.shop = new ShopIdAndNameView(shop);
        this.goodsSku = goodsSku;
        this.id = po.getId();
        this.name = po.getName();
        this.beginTime = po.getBeginTime().toString();
        this.payTime = df.format(po.getPayTime());
        this.endTime = df.format(po.getEndTime());
        this.state = po.getState().toString();
        this.quantity =po.getQuantity();
        this.advancePayPrice = po.getAdvancePayPrice();
        this.restPayPrice = po.getRestPayPrice();
        this.gmtCreate = df.format(po.getGmtCreate());
        this.gmtModified = df.format(po.getGmtModified());
    }
}
