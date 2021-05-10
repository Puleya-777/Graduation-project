package com.order.model;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class PageWrap {
    private final Integer page;
    private final Integer pageSize;
    private final Long total;
    private final Integer pages;
    private final List<?> list;

    private PageWrap(PageInfo<?> info, List<?> list) {
        this.page = info.getPageNum();
        this.pageSize = info.getPageSize();
        this.total = info.getTotal();
        this.pages = info.getPages();
        this.list = list;
    }

    public static PageWrap of(PageInfo<?> info, List<?> list) {
        return new PageWrap(info, list);
    }

    public static PageWrap of(PageInfo<?> info) {
        return new PageWrap(info, info.getList());
    }
}
