package com.example.model.state;

public class ShopStateVo {
    private Long Code;

    private String name;
    public ShopStateVo(ShopState state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
