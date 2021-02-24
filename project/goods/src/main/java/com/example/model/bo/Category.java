package com.example.model.bo;

import com.example.model.VoObject;
import com.example.model.po.CategoryPo;
import lombok.Data;

@Data
public class Category implements VoObject {

    Long id;

    String name;

    Long pid;

    String gmtCreate;

    String gmtModified;

    public Category(CategoryPo categoryPo){
        id=categoryPo.getId();
        name=categoryPo.getName();
        pid=categoryPo.getPid();
        if(categoryPo.getGmtCreate()!=null)
            gmtCreate=categoryPo.getGmtCreate().toString();
        if(categoryPo.getGmtModified()!=null)
            gmtModified=categoryPo.getGmtModified().toString();
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
