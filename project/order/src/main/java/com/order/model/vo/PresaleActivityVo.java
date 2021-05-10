package com.order.model.vo;


import lombok.Data;

@Data
public class PresaleActivityVo {

    private String name;

    private Integer quantity;

    private Long advancePayPrice;

    private Long restPayPrice;

    private Long shopId;

    private Long goodsSkuId;

    private Byte state;

    //@DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private String beginTime;

    //@DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private String payTime;

    //@DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private String endTime;
}
