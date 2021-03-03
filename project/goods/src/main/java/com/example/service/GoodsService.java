package com.example.service;

import com.example.dao.CategoryDao;
import com.example.dao.GoodsDao;
import com.example.model.VoObject;
import com.example.model.bo.*;
import com.example.model.po.*;
import com.example.model.vo.SimpleRetSku;
import com.example.model.vo.SkuVo;
import com.example.model.vo.SpuVo;
import com.example.repository.*;
import com.example.util.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
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
    ShopRepository shopRepository;
    @Resource
    CategoryRepository categoryRepository;
    @Autowired
    OssFileUtil ossFileUtil;
    @Autowired
    NacosHelp nacosHelp;
    @Autowired
    CommonUtil commonUtil;

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

    public Mono<ReturnObject> getAllSpuInShop(Long shopId,Integer page,Integer pageSize) {
//        return spuRepository.findAllByShopId(shopId)
//                .flatMap(spuPo -> {
//                    Mono<Brand> brandMono= brandRepository.findById(spuPo.getBrandId()).defaultIfEmpty(new BrandPo()).map(Brand::new);
//                    Mono<Category> categoryMono= categoryRepository.findById(spuPo.getCategoryId()).defaultIfEmpty(new CategoryPo()).map(Category::new);
//                    Mono<Shop> shopMono= shopRepository.findById(spuPo.getShopId()).defaultIfEmpty(new ShopPo()).map(Shop::new);
//                    Mono<List<SimpleRetSku>> simpleRetSkuFlux= skuRepository.findAllByGoodsSpuId(spuPo.getId())
//                            .map(SimpleRetSku::new).collect(Collectors.toList());
////                    System.out.println(spuPo);
//                    return Mono.zip(brandMono,categoryMono,shopMono,simpleRetSkuFlux).map(tuple-> {
//                        Spu spu = new Spu(spuPo, tuple.getT1(), tuple.getT2(), tuple.getT3());
//                        spu.setFreight(nacosHelp.findFreightById(spuPo.getFreightId()));
//                        spu.setSkuList(tuple.getT4());
//                        System.out.println(spu);
//
//                        return spu;
//                    });
//                        }
//                ).collect(Collectors.toList())
//                .map(list->{System.out.println(list);return list;}).map(list->commonUtil.listToPage(list,page,pageSize))
//                .map(ReturnObject::new);
        return spuRepository.findAllByShopId(shopId)
                .flatMap(spuPo -> {
                    return goodsDao.getSpuInfoById(spuPo.getId());
                }).collect(Collectors.toList())
                .map(list->commonUtil.listToPage(list,page,pageSize)).map(ReturnObject::new);
    }
}
