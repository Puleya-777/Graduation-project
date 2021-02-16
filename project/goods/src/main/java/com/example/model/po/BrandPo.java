package com.example.model.po;

import com.example.model.vo.BrandVo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("brand")
@NoArgsConstructor
public class BrandPo {

    @Id
    Long id;

    String name;

    String detail;

    String imageUrl;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public BrandPo(BrandVo brandVo){
        name=brandVo.getName();
        detail=brandVo.getDetail();
    }

    public void setFromVo(BrandVo brandVo){
        if(brandVo.getDetail()!=null){
            this.detail=brandVo.getDetail();
        }
        if(brandVo.getName()!=null){
            this.name=brandVo.getName();
        }
    }

}
