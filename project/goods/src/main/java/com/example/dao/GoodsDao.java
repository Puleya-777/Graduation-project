package com.example.dao;

import com.example.model.VoObject;
import com.example.model.bo.*;
import com.example.model.po.*;
import com.example.model.vo.SimpleRetSku;
import com.example.repository.*;
import com.example.util.NacosHelp;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class GoodsDao {

    @Resource
    SkuRepository skuRepository;
    @Resource
    SpuRepository spuRepository;
    @Resource
    BrandRepository brandRepository;
    @Resource
    ShopRepository shopRepository;
    @Resource
    CategoryRepository categoryRepository;
    @Autowired
    NacosHelp nacosHelp;

    public Mono<Sku> getSkuInfoById(Long id) {

        Mono<SkuPo> skuPoMono=skuRepository.findById(id).defaultIfEmpty(new SkuPo());
        Mono<SpuPo> spuPoMono=skuPoMono.flatMap(skuPo -> {
            if(skuPo.getGoodsSpuId()!=null){
                return  spuRepository.findById(skuPo.getGoodsSpuId()).defaultIfEmpty(new SpuPo());
            }else{
                return Mono.just(new SpuPo());
            }
        });
        Mono<Brand> brandMono=spuPoMono.flatMap(spuPo -> {
            if(spuPo.getBrandId()!=null){
                return  brandRepository.findById(spuPo.getBrandId()).defaultIfEmpty(new BrandPo());
            }else{
                return Mono.just(new BrandPo());
            }
        }).map(Brand::new);
        Mono<Category> categoryMono=spuPoMono.flatMap(spuPo -> {
            if(spuPo.getCategoryId()!=null){
                return  categoryRepository.findById(spuPo.getCategoryId()).defaultIfEmpty(new CategoryPo());
            }else{
                return Mono.just(new CategoryPo());
            }
        }).map(Category::new);
        Mono<Shop> shopMono=spuPoMono.flatMap(spuPo -> {
            if(spuPo.getShopId()!=null){
                return  shopRepository.findById(spuPo.getShopId()).defaultIfEmpty(new ShopPo());
            }else{
                return Mono.just(new ShopPo());
            }
        }).map(Shop::new);
//        System.out.println(id);
//        System.out.println(skuPoMono.block());
//        System.out.println(spuPoMono.block());
//        System.out.println(brandMono.block());
//        System.out.println(categoryMono.block());
//        System.out.println(shopMono.block());

        return Mono.zip(skuPoMono,spuPoMono,brandMono,categoryMono,shopMono).map(tuple->{
            System.out.println("has enter");
            Spu spu=new Spu(tuple.getT2(),tuple.getT3(),tuple.getT4(),tuple.getT5());
            return new Sku(tuple.getT1(),spu);
        });

    }

    public Mono<ReturnObject> addSkuToSpu(SkuPo skuPo) {
        return skuRepository.findBySkuSn(skuPo.getSkuSn()).defaultIfEmpty(new SkuPo())
                .flatMap(skuPoRes->{
                    if(skuPoRes.getId()!=null){
                        return Mono.just(new ReturnObject<>(ResponseCode.SKUSN_SAME));
                    }else{
                        return skuRepository.save(skuPo).map(SimpleRetSku::new)
                                .map(ReturnObject::new);
                    }
                });
    }


    public Mono<SkuPo> findSkuPo(Long skuId) {
        return skuRepository.findById(skuId);
    }

    public Mono<SkuPo> updateSku(SkuPo skuPo) {
        return skuRepository.save(skuPo);
    }

    public Mono<Integer> deleteSku(Long skuId) {
        return skuRepository.deleteSkuPoById(skuId);
    }

    public Mono<Spu> getSpuInfoById(Long id) {
        Mono<SpuPo> spuPoMono=spuRepository.findById(id);
        Mono<Brand> brandMono=spuPoMono.flatMap(spuPo -> {
            if(spuPo.getBrandId()!=null)
                return brandRepository.findById(spuPo.getBrandId()).defaultIfEmpty(new BrandPo());
            else{
                return Mono.just(new BrandPo());
            }
        }).map(Brand::new);
        Mono<Category> categoryMono=spuPoMono.flatMap(spuPo -> {
            if(spuPo.getCategoryId()!=null)
                return categoryRepository.findById(spuPo.getCategoryId()).defaultIfEmpty(new CategoryPo());
            else
                return Mono.just(new CategoryPo());
        }).map(Category::new);
        Mono<Shop> shopMono=spuPoMono.flatMap(spuPo -> {
            if(spuPo.getShopId()!=null)
                return shopRepository.findById(spuPo.getShopId()).defaultIfEmpty(new ShopPo());
            else
                return Mono.just(new ShopPo());
        }).map(Shop::new);
        Mono<List<SimpleRetSku>> simpleRetSkuFlux=spuPoMono.flatMap(spuPo -> skuRepository.findAllByGoodsSpuId(spuPo.getId())
                .map(SimpleRetSku::new).collect(Collectors.toList()));
        return Mono.zip(spuPoMono,brandMono,categoryMono,shopMono,simpleRetSkuFlux).map(tuple->{
            Spu spu=new Spu(tuple.getT1(),tuple.getT2(),tuple.getT3(),tuple.getT4());
            spu.setFreight(nacosHelp.findFreightById(tuple.getT1().getFreightId()));
            spu.setSkuList(tuple.getT5());
            return spu;
        });
    }
}
