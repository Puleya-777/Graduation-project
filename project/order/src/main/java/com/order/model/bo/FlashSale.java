package com.order.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.order.model.po.FlashSaleItemPo;
import com.order.model.po.FlashSalePo;
import com.order.model.vo.FlashSaleCreatorValidation;
import com.order.model.vo.FlashSaleItemCreatorValidation;
import com.order.model.vo.FlashSaleModifierValidation;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class FlashSale {
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum State {
        OFFLINE(0, "已下线"),
        ONLINE(1, "已上线"),
        DELETED(2, "已删除");
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
            if (code == null) return OFFLINE;
            else if (code.equals(0)) return OFFLINE;
            else if (code.equals(1)) return ONLINE;
            else if (code.equals(2)) return DELETED;
            return OFFLINE;
        }
    }

    private Long id;
    private LocalDateTime flashDate;
    private Long timeSegId;
    private State state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public FlashSalePo toFlashSalePo() {
        FlashSalePo po = new FlashSalePo();
        po.setId(id);
        po.setFlashDate(flashDate);
        po.setTimeSegId(timeSegId);
        po.setState(state == null ? null : state.getCode());
        po.setGmtCreate(gmtCreate);
        po.setGmtModified(gmtModified);
        return po;
    }

    public FlashSale(FlashSalePo po) {
        this.id = po.getId();
        this.flashDate = po.getFlashDate();
        this.timeSegId = po.getTimeSegId();
        this.state = State.fromCode(po.getState());
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }

    public FlashSale(FlashSaleCreatorValidation vo) {
        this.flashDate = Timestamp.valueOf(vo.getFlashDate()).toLocalDateTime();
    }

    public FlashSale(FlashSaleModifierValidation vo) {
        this.flashDate = Timestamp.valueOf(vo.getFlashDate()).toLocalDateTime();
        this.gmtModified = LocalDateTime.now();
    }

    @Data
    public static class Item {
        private Long id;
        private Long saleId;
        private Long goodsSkuId;
        private Long price;
        private Integer quantity;
        private LocalDateTime gmtCreate;
        private LocalDateTime gmtModified;

        public Item(FlashSaleItemCreatorValidation vo) {
            this.goodsSkuId = vo.getSkuId();
            this.price = vo.getPrice();
            this.quantity = vo.getQuantity();
        }

        public Item(FlashSaleItemPo po) {
            this.id = po.getId();
            this.saleId = po.getSaleId();
            this.goodsSkuId = po.getGoodsSkuId();
            this.price = po.getPrice();
            this.quantity = po.getQuantity();
            this.gmtCreate = po.getGmtCreate();
            this.gmtModified = po.getGmtModified();
        }

        public FlashSaleItemPo toItemPo() {
            FlashSaleItemPo po = new FlashSaleItemPo();
            po.setId(id);
            po.setSaleId(saleId);
            po.setGoodsSkuId(goodsSkuId);
            po.setPrice(price);
            po.setQuantity(quantity);
            po.setGmtCreate(gmtCreate);
            po.setGmtModified(gmtModified);
            return po;
        }
    }
}
