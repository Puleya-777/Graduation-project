package com.order.model.vo;

import com.order.model.po.FreightModelPo;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class FreightModelReturnVo implements com.example.model.VoObject {
    private Long id;

    private String name;

    private Byte type;

    private Integer unit;

    private Boolean defaultModel=false;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    /**
     * 用FreightModelPo对象建立Vo对象
     */
    public FreightModelReturnVo(FreightModelPo freightModelPo) {
        this.id = freightModelPo.getId();
        this.name = freightModelPo.getName();
        this.type = freightModelPo.getType();
        this.unit= freightModelPo.getUnit();
        this.gmtCreate = freightModelPo.getGmtCreate();
        this.gmtModified = freightModelPo.getGmtModified();
        if(freightModelPo.getDefaultModel()==1){
            this.defaultModel=true;
        }
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public FreightModelPo createPo() {
        FreightModelPo freightModelPo=new FreightModelPo();
        freightModelPo.setId(this.id);
        freightModelPo.setName(this.name);
        freightModelPo.setType(this.type);
        freightModelPo.setUnit(this.unit);
        freightModelPo.setGmtCreate(this.gmtCreate);
        freightModelPo.setGmtModified(this.gmtModified);
        Byte a=1;
        if(this.defaultModel=true){
            freightModelPo.setDefaultModel(a);
        }
        return  freightModelPo;
    }
}
