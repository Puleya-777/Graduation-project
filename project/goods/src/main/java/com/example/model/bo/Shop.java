package com.example.model.bo;

import com.example.model.po.ShopPo;
import lombok.Data;

@Data
public class Shop {

    Long id;

    String name;

    public Shop(ShopPo shopPo){
        id=shopPo.getId();
        name=shopPo.getName();
    }

}
