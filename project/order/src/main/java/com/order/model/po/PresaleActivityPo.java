package com.order.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("presale_activity")
@Data
public class PresaleActivityPo {
    @Id
    private Long id;//主键

    private String name;//活动名称

    private LocalDateTime beginTime;//活动开始时间

    private LocalDateTime payTime;//开始支付尾款时间

    private LocalDateTime endTime;//活动结束时间

    private Integer state;//活动状态 0 已新建 1 被取消 2 已删除

    private Long shopId;//店铺ID

    private Long goodsSkuId;//商品SKU ID

    private Integer quantity;//互动库存量

    private Long advancePayPrice;//定金

    private Long restPayPrice;//尾款

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}