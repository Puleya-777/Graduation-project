package com.example.service;

import com.example.dao.CategoryDao;
import com.example.dao.GoodsDao;
import com.example.model.VoObject;
import com.example.model.po.FloatPricePo;
import com.example.model.po.SkuPo;
import com.example.model.po.SpuPo;
import com.example.model.vo.SkuVo;
import com.example.repository.BrandRepository;
import com.example.repository.CategoryRepository;
import com.example.repository.SkuRepository;
import com.example.repository.SpuRepository;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;
    @Autowired
    CategoryDao categoryDao;

    @Autowired
    SpuRepository spuRepository;
    @Autowired
    SkuRepository skuRepository;
    @Autowired
    BrandRepository brandRepository;

    @Resource
    CategoryRepository categoryRepository;

    public Mono<ReturnObject<VoObject>> getSkuInfoById(Long id) {

        return goodsDao.getSkuInfoById(id).map(ReturnObject::new);

    }

    public Mono<ReturnObject> addSkuToSpu(Integer shopId, Long id, SkuPo skuPo) {
        return goodsDao.addSkuToSpu(skuPo);
    }

    public Mono<ReturnObject> updatePicToSku(Long skuId, MultipartFile file) {
        return goodsDao.findSkuPo(skuId).flatMap(skuPo -> {
            if(skuPo.getImageUrl()!=null){

            }
            String fileName = file.getOriginalFilename();
            String filePath = "/Users/itinypocket/workspace/temp/";
            File dest = new File(filePath + fileName);
            try {
                file.transferTo(dest);
            } catch (IOException e) {
                return Mono.just(new ReturnObject(ResponseCode.IMG_UPDATE_FAIL));
            }

            skuPo.setImageUrl(filePath+fileName);
            return goodsDao.updateSku(skuPo).map(ReturnObject::new);
        });
    }

    public Mono<ReturnObject> deleteSku(Long skuId) {
        return goodsDao.deleteSku(skuId).map(ReturnObject::new);
    }

    public Mono<ReturnObject> putSku(Long skuId, SkuVo skuVo) {
        return goodsDao.findSkuPo(skuId).defaultIfEmpty(new SkuPo()).map(skuPo -> {
            if(skuPo.getId()==null){
                return ResponseCode.RESOURCE_ID_NOTEXIST;
            }else{
                skuPo.setFromVo(skuVo);
                goodsDao.updateSku(skuPo);
                return skuPo;
            }
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> queryCategoryRelation(Long id) {
        return categoryDao.queryCategoryRelation(id).map(ReturnObject::new);
    }

    public Mono<ReturnObject> getSpuInfoById(Long id) {
        return goodsDao.getSpuInfoById(id).map(ReturnObject::new);
    }

    public Mono<ReturnObject> modifyGoodsState(Long id,Integer state) {
        return skuRepository.findById(id).defaultIfEmpty(new SkuPo()).map(spuPo -> {
            if(spuPo.getId()==null){
                return ResponseCode.RESOURCE_ID_NOTEXIST;
            }else{
                spuPo.setState(state);
                skuRepository.save(spuPo);
                return spuPo;
            }
        }).map(ReturnObject::new);
    }


    public Mono<ReturnObject> addSpuCategory(Long spuId, Long id) {
        return spuRepository.findById(spuId).defaultIfEmpty(new SpuPo())
                .flatMap(spuPo -> {
                    if(spuPo.getId()==null){
                        return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }else{
                        spuPo.setCategoryId(id);
                        return spuRepository.save(spuPo);
                    }
                }).map(ReturnObject::new);
    }


    public Mono<ReturnObject> addSpuBrand(Long spuId, Long brandId) {
        return spuRepository.findById(spuId).defaultIfEmpty(new SpuPo())
                .flatMap(spuPo -> {
                    if(spuPo.getId()==null){
                        return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }else{
                        spuPo.setBrandId(brandId);
                        return spuRepository.save(spuPo);
                    }
                }).map(ReturnObject::new);
    }
}
