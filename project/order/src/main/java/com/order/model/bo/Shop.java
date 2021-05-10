package com.order.model.bo;

import com.example.model.VoObject;
import com.order.model.po.ShopPo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Shop implements VoObject {

    Long id;

    String name;

    Integer state;

    String gmtCreate;

    String gmtModified;

    public Shop(ShopPo shopPo){
        id=shopPo.getId();
        name=shopPo.getName();
        state=shopPo.getState();
        if(shopPo.getGmtCreate()!=null)
            gmtCreate=shopPo.getGmtCreate().toString();
        if(shopPo.getGmtModified()!=null)
            gmtModified=shopPo.getGmtModified().toString();
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
