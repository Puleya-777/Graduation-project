package com.example.model.state;

public class CouponStateVo {
    private Long Code;

    private String name;
    public CouponStateVo(CouponState state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }


}
