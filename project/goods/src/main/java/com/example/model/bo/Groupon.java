package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.GrouponActivityPo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Groupon implements VoObject {

    Long id;

    String name;

    String beginTime;

    String endTime;
    
    Integer state;

    public Groupon(GrouponActivityPo grouponActivityPo){
        id=grouponActivityPo.getId();
        name=grouponActivityPo.getName();
        beginTime=grouponActivityPo.getBeginTime().toString();
        endTime=grouponActivityPo.getEndTime().toString();
        state=grouponActivityPo.getState();
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public enum State {
        NEW(0, "新注册"),
        NORM(1, "正常"),
        FORBID(2, "封禁"),
        DELETE(3, "废弃");

        private static final Map<Integer, Groupon.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Groupon.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Groupon.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
