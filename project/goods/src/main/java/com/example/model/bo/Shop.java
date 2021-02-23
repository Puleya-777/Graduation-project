package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.ShopPo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Shop implements VoObject {

    Long id;

    String name;

    public Shop(ShopPo shopPo){
        id=shopPo.getId();
        name=shopPo.getName();
    }

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
