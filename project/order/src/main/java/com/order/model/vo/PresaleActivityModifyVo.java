package com.order.model.vo;

import lombok.Data;

@Data
public class PresaleActivityModifyVo
{

    private String name;

    private Integer quantity;

    private Long advancePayPrice;

    private Long restPayPrice;

    private Long shopId;

    //@DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private String beginTime;

    //@DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private String payTime;

    //@DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private String endTime;

    public PresaleActivityModifyVo(String name, Integer quantity, Long advancePayPrice, Long restPayPrice,
                                   Long shopId, String beginTime, String payTime, String endTime)
    {
        this.name=name;
        this.quantity=quantity;
        this.advancePayPrice=advancePayPrice;
        this.restPayPrice=restPayPrice;
        this.shopId=shopId;
        this.beginTime=beginTime;
        this.payTime=payTime;
        this.endTime=endTime;
    }
}
