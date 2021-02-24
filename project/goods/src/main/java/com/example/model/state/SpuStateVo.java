package com.example.model.state;

public class SpuStateVo {
    private Long Code;

    private String name;
    public SpuStateVo(SpuState state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
