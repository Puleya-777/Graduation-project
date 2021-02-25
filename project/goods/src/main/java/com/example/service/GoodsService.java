package com.example.service;

import com.example.dao.CategoryDao;
import com.example.dao.GoodsDao;
import com.example.model.VoObject;
import com.example.model.bo.Sku;
import com.example.model.bo.Spu;
import com.example.model.po.FloatPricePo;
import com.example.model.po.SkuPo;
import com.example.model.po.SpuPo;
import com.example.model.vo.SkuVo;
import com.example.model.vo.SpuVo;
import com.example.repository.BrandRepository;
import com.example.repository.CategoryRepository;
import com.example.repository.SkuRepository;
import com.example.repository.SpuRepository;
import com.example.util.OssFileUtil;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
    @Autowired
    OssFileUtil ossFileUtil;

    public Mono<ReturnObject<VoObject>> getSkuInfoById(Long id) {

        return goodsDao.getSkuInfoById(id).map(ReturnObject::new);

    }

    public Mono<ReturnObject> addSkuToSpu(Integer shopId, Long id, SkuPo skuPo) {
        return goodsDao.addSkuToSpu(skuPo);
    }

    public Mono<ReturnObject> updatePicToSku(Long skuId, MultipartFile file) {
        return skuRepository.findById(skuId).flatMap(skuPo -> {
            String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            Random random=new Random();
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<10;i++){
                int number=random.nextInt(62);
                sb.append(str.charAt(number));
            }
            int begin = file.getOriginalFilename().indexOf(".");
            int last = file.getOriginalFilename().length();
            sb.append(file.getOriginalFilename(), begin, last);
            String filename=sb.toString();
            try {
                return ossFileUtil.uploadAliyun(file,filename).flatMap(url->{
                    skuPo.setImageUrl(url);
                    return skuRepository.save(skuPo).map(a->{
                        if(a!=null) {
                            return new ReturnObject<>();
                        }
                        return null;
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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

    public Mono<ReturnObject> querySku(String skuSn, Long spuId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
//        Mono<List<VoObject>> skuPos=skuRepository.findAllByGoodsSpuId(spuId).filter(skuPo -> skuPo.getSkuSn().equals(skuSn))
//                .map(Sku::new).collect(Collectors.toList());
        Mono<List<VoObject>> skuPos=skuRepository.findAll()
                .map(Sku::new).collect(Collectors.toList());
        return skuPos.map(list->{
//            PageInfo<VoObject> pageInfo = PageInfo.of(list);
            PageInfo<VoObject> retPage=new PageInfo<>(list);
            retPage.setPages(page);
            retPage.setPageNum(page);
            retPage.setPageSize(pageSize);
            retPage.setTotal(pageSize);
//            System.out.println("\n..........nn...........\n"+retPage.toString());
            return new ReturnObject(retPage);
        });


    }

    public Mono<ReturnObject> modifySpu(Long id, SpuVo spuVo) {
        return spuRepository.findById(id).defaultIfEmpty(new SpuPo()).flatMap(spuPo -> {
            if(spuPo.getId()==null){
                return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else{
                spuPo.setName(spuVo.getName());
                spuPo.setDetail(spuVo.getDescription());
                spuPo.setSpec(spuVo.getSpecs());
                return spuRepository.save(spuPo);
            }
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> deleteSpu(Long id) {
        return spuRepository.findById(id).defaultIfEmpty(new SpuPo()).flatMap(spuPo -> {
            if(spuPo.getId()==null){
                return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else{
                return spuRepository.deleteSpuPoById(id);
            }
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> newSpu(Long id, SpuVo spuVo) {
        SpuPo spuPo=new SpuPo(spuVo);
        spuPo.setShopId(id);
        return spuRepository.save(spuPo).map(Spu::new).map(ReturnObject::new);
    }
}
