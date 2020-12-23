package com.example.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("brand")
public class BrandPo {

    @Id
    Long id;

    String name;

    String detail;

    String imageUrl;

}
