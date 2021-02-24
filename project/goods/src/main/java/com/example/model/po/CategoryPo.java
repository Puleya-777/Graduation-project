package com.example.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("goods_category")
@Data
public class CategoryPo{

    @Id
    Long id;

    String name;

    //上一级分类id
    Long pid;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

}
