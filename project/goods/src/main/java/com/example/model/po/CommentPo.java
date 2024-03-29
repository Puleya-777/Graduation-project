package com.example.model.po;

import com.example.model.vo.CommentVo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("comment")
@NoArgsConstructor
public class CommentPo {

    @Id
    Long id;

    Long customerId;

    Long goodsSkuId;

    Long orderitemId;

    Integer type;

    String content;

    Integer state;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public CommentPo(CommentVo commentVo){
        type=commentVo.getType();
        content=commentVo.getContent();
        state=commentVo.getState();
    }

}
