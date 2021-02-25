package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.ShopPo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
