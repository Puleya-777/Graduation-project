package com.order.model.bo;

//import com.goods.model.po.GrouponActivityPo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.order.model.po.PresaleActivityPo;
import com.order.model.vo.PresaleActivityVo;
import com.order.model.vo.ReturnGoodsSkuVo;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class PresaleActivity {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum State implements Serializable {
        OFFLINE(0, "已下线"),
        ONLINE(1,"已上线"),
        DELETE(2, "已删除");
        private final Integer code;
        private final String name;

        State(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static State fromCode(Integer code) {
            switch (code) {
                case 0:
                    return OFFLINE;
                case 1:
                    return ONLINE;
                case 2:
                    return DELETE;
            }
            return null;
        }
    }

    private Long id;
    private String name;
    private LocalDateTime beginTime;
    private LocalDateTime payTime;
    private LocalDateTime endTime;
    private State state;
    private Shop shop;
    private ReturnGoodsSkuVo goodsSku;
    private Integer quantity;
    private Long advancePayPrice;
    private Long restPayPrice;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public PresaleActivity(){
    }

    public PresaleActivity( Shop shop,ReturnGoodsSkuVo goodsSku,PresaleActivityVo vo)
    {
        this.shop = shop;
        this.goodsSku = goodsSku;
        this.name = vo.getName();
        this.quantity = vo.getQuantity();
        this.advancePayPrice = vo.getAdvancePayPrice();
        this.restPayPrice = vo.getRestPayPrice();
        this.state = State.fromCode(vo.getState().intValue());
        this.beginTime = Timestamp.valueOf(vo.getBeginTime()).toLocalDateTime();
        this.payTime = Timestamp.valueOf(vo.getPayTime()).toLocalDateTime();
        this.endTime = Timestamp.valueOf(vo.getEndTime()).toLocalDateTime();
    }

    public PresaleActivity(Shop shop,ReturnGoodsSkuVo goodsSku,PresaleActivityPo po)
    {
        this.shop = shop;
        this.goodsSku = goodsSku;
        this.id = po.getId();
        this.name = po.getName();
        this.beginTime = po.getBeginTime();
        this.payTime = po.getPayTime();
        this.endTime = po.getEndTime();
        this.state = State.fromCode(po.getState().intValue());
        this.quantity =po.getQuantity();
        this.advancePayPrice = po.getAdvancePayPrice();
        this.restPayPrice = po.getRestPayPrice();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    public PresaleActivityPo getPresaleActivityPo() {
        PresaleActivityPo po = new PresaleActivityPo();
        po.setId(this.id);
        po.setName(this.name);
        po.setShopId(this.shop.getId());
        po.setState(this.state.getCode());
        po.setGoodsSkuId(this.goodsSku.getId());
        po.setPayTime(this.payTime);
        po.setBeginTime(this.beginTime);
        po.setEndTime(this.endTime);
        po.setQuantity(this.quantity);
        po.setAdvancePayPrice(this.advancePayPrice);
        po.setRestPayPrice(this.restPayPrice);
        po.setGmtCreate(LocalDateTime.now());
        return po;
    }
}
