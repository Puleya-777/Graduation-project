package com.example.service;

import com.example.dao.GoodsDao;
import com.example.model.VoObject;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public Mono<ReturnObject<VoObject>> getSkuInfoById(Long id) {

        return goodsDao.getSkuInfoById(id);

    }
}
