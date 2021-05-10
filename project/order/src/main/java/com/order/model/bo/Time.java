package com.order.model.bo;

import com.example.model.VoObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.order.model.po.TimeSegmentPo;
import com.order.model.vo.TimeRetVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Time implements VoObject {
    private Long id;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Type type;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public enum Type {
        ADVERTISE(0, "广告时段"),
        FLASHSALE(1, "秒杀时段");

        private static final Map<Integer, Type> stateMap;

        static {
            // 由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Type enumType : values()) {
                stateMap.put(enumType.code, enumType);
            }
        }

        private int code;
        private String description;

        Type(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 通过code获取枚举类型
         */
        public static Type getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        /**
         * 获取所有的枚举类型
         */
        public static List getAllType() {
            List allType = Lists.newArrayList();
            for (Type enumType : values()) {
                allType.add(ImmutableMap.<String, Object> builder()
                        .put("code", String.valueOf(enumType.code))
                        .put("name", enumType.getDescription())
                        .build());
            }
            return allType;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * po属性之外的属性
     */
    public Time() {}

    /**
     * 通过vo构造bo
     */
    public Time(LocalDateTime begin, LocalDateTime end) {
        this.setBeginTime(begin);
        this.setEndTime(end);
    }

    /**
     * 通过po构造bo
     */
    public Time(TimeSegmentPo po) {
        this.setId(po.getId());
        this.setBeginTime(po.getBeginTime());
        this.setEndTime(po.getEndTime());
        if (po.getType() != null) {
            this.setType(Type.getTypeByCode(po.getType().intValue()));
        }
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
    }

    /**
     * 构造po对象
     */
    public TimeSegmentPo createPo() {
        TimeSegmentPo po = new TimeSegmentPo();
        po.setId(this.getId());
        po.setBeginTime(this.getBeginTime());
        po.setEndTime(this.getEndTime());
        if (this.getType() != null) {
            po.setType(this.getType().getCode().byteValue());
        }
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }

    /**
     * 构造vo对象
     */
    @Override
    public Object createVo() {
        TimeRetVo vo = new TimeRetVo(this);
        return vo;
    }

    /**
     * 构造简单vo对象
     */
    @Override
    public Object createSimpleVo() {
        TimeRetVo vo = new TimeRetVo(this);
        return vo;
    }
}