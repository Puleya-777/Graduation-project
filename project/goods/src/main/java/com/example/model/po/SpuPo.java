package com.example.model.po;

import com.example.model.bo.Spec;
import com.example.model.vo.SpuVo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("goods_spu")
@Data
@NoArgsConstructor
public class SpuPo {

    @Id
    Long id;    //主键

    String name;    //商品名称

    Long brandId;   //品牌Id

    Long categoryId;    //分类Id

    Long freightId;     //运费模板Id

    Long shopId;        //店铺Id

    String goodsSn;       //商品编号

    String detail;      //商品描述

    String imageUrl;        //图片链接

    String spec;        //可选规格JSON

    Boolean disabled;    //禁止访问：0不启用disabled，1启用disabled

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public SpuPo(SpuVo spuVo){
        name=spuVo.getName();
        detail=spuVo.getDescription();
        spec=spuVo.getSpecs();
    }

}
