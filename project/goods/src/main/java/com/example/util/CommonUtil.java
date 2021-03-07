package com.example.util;

import com.example.model.bo.CouponActivity;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommonUtil<T> {

    public PageInfo<T> listToPage(List<T> list,Integer page,Integer pageSize){
        int left=(page-1)*pageSize;
        int right=page*pageSize;
        if(list.size()>=right){
            list=list.subList(left,right);
        }else{
            if(left>=list.size()){
                list=new ArrayList<>();
            }else{
                list=list.subList(left,list.size());
            }
        }
        PageInfo<T> retPage=new PageInfo<>(list);
        retPage.setPages(page);
        retPage.setPageNum(page);
        retPage.setPageSize(pageSize);
        retPage.setTotal(list.size());
        return retPage;
    }

}
