package com.example.model.state;

import com.example.model.bo.Comment;

import java.util.HashMap;
import java.util.Map;

public enum CommentState {
    NEW(0, "新注册"),
    NORM(1, "正常"),
    FORBID(2, "封禁"),
    DELETE(3, "废弃");

    private static final Map<Integer, CommentState> stateMap;

    static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
        stateMap = new HashMap();
        for (CommentState enum1 : values()) {
            stateMap.put(enum1.code, enum1);
        }
    }

    private int code;
    private String description;

    CommentState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CommentState getTypeByCode(Integer code) {
        return stateMap.get(code);
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}