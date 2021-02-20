package com.example.model.po;

import com.example.model.vo.ShopVo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("shop")
@NoArgsConstructor
public class ShopPo {

    @Id
    Long id;

    String name;

    Integer state;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public ShopPo(ShopVo shopVo){
        name=shopVo.getName();
    }

}
