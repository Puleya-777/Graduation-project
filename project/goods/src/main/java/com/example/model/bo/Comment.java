package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.CommentPo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment implements VoObject {

    Long id;

    User customer;

    Long goodsSkuId;

    Integer type;

    String content;

    Integer state;

    LocalDateTime gmtCreate;

    LocalDateTime gmtModified;

    public Comment(CommentPo commentPo){
        id=commentPo.getId();
        customer =new User();
        customer.setId(commentPo.getCustomerId());
        goodsSkuId=commentPo.getGoodsSkuId();
        type=commentPo.getType();
        content=commentPo.getContent();
        state=commentPo.getState();
        gmtCreate=commentPo.getGmtCreate();
        gmtModified=commentPo.getGmtModified();
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

}
