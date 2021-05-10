package com.order.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopRetVo {

    private Long id;

    private String name;

    private Integer state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
