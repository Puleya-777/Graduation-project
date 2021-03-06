package com.example.util;

import com.example.model.bo.CouponActivity;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommonUtil<T> {

    public PageInfo<T> listToPage(List<T> list,Integer page,Integer pageSize){
        list=list.subList((page-1)*pageSize,page*pageSize-1);
        PageInfo<T> retPage=new PageInfo<>(list);
        retPage.setPages(page);
        retPage.setPageNum(page);
        retPage.setPageSize(pageSize);
        retPage.setTotal(list.size());
        return retPage;
    }

}
