package com.example.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Freight {

    Long id;

    String name;

    Integer type;

    Long unit;

    boolean defaultFreight;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

}
