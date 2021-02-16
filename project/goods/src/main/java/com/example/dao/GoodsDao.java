package com.example.dao;

import com.example.model.VoObject;
import com.example.model.bo.*;
import com.example.model.po.SkuPo;
import com.example.model.po.SpuPo;
import com.example.model.vo.SimpleRetSku;
import com.example.repository.*;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Optional;

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

    public Mono<Sku> getSkuInfoById(Long id) {

        Mono<SkuPo> skuPoMono=skuRepository.findById(id);
        Mono<SpuPo> spuPoMono=skuPoMono.flatMap(skuPo -> spuRepository.findById(skuPo.getGoodsSpuId()));
        Mono<Brand> brandMono=spuPoMono.flatMap(spuPo -> brandRepository.findById(spuPo.getBrandId()).map(Brand::new));
        Mono<Category> categoryMono=spuPoMono.flatMap(spuPo -> categoryRepository.findById(spuPo.getCategoryId()).map(Category::new));
        Mono<Shop> shopMono=spuPoMono.flatMap(spuPo -> shopRepository.findById(spuPo.getShopId()).map(Shop::new));

        return Mono.zip(skuPoMono,spuPoMono,brandMono,categoryMono,shopMono).map(tuple->{
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
        Mono<Brand> brandMono=spuPoMono.flatMap(spuPo -> brandRepository.findById(spuPo.getBrandId()).map(Brand::new));
        Mono<Category> categoryMono=spuPoMono.flatMap(spuPo -> categoryRepository.findById(spuPo.getCategoryId()).map(Category::new));
        Mono<Shop> shopMono=spuPoMono.flatMap(spuPo -> shopRepository.findById(spuPo.getShopId()).map(Shop::new));
        return Mono.zip(spuPoMono,brandMono,categoryMono,shopMono).map(tuple->{
            Spu spu=new Spu(tuple.getT1(),tuple.getT2(),tuple.getT3(),tuple.getT4());
            return spu;
        });
    }
}
