package com.example.service;

import com.example.dao.GoodsDao;
import com.example.model.VoObject;
import com.example.model.po.SkuPo;
import com.example.model.vo.SkuVo;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public Mono<ReturnObject<VoObject>> getSkuInfoById(Long id) {

        return goodsDao.getSkuInfoById(id).map(ReturnObject::new);

    }

    public Mono<ReturnObject> addSkuToSpu(Integer shopId, Long id, SkuPo skuPo) {
        return goodsDao.addSkuToSpu(skuPo);
    }

    public Mono<ReturnObject> updatePicToSku(Long skuId, MultipartFile file) {
        return goodsDao.findSkuPo(skuId).map(skuPo -> {
            if(skuPo.getImageUrl()!=null){

            }
            String fileName = file.getOriginalFilename();
            String filePath = "/Users/itinypocket/workspace/temp/";
            File dest = new File(filePath + fileName);
            file.transferTo(dest);

            skuPo.setImageUrl(filePath+fileName);
            return goodsDao.updateSku(skuPo).map(ReturnObject::new);
        }).onErrorReturn(null);
    }

    public Mono<ReturnObject> deleteSku(Integer skuId) {
        return goodsDao.deleteSku(skuId);
    }
}
