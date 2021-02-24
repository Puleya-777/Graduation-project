package com.example.model.state;

public class GrouponStateVo {
    private Long Code;

    private String name;
    public GrouponStateVo(GrouponState state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
