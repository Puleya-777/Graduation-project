package com.example.service;

import com.example.model.bo.Shop;
import com.example.model.po.ShopPo;
import com.example.model.vo.AuditShopVo;
import com.example.model.vo.ShopVo;
import com.example.repository.ShopRepository;
import com.example.util.CommonUtil;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class ShopService {

    @Autowired
    ShopRepository shopRepository;
    @Autowired
    CommonUtil commonUtil;

    public Mono<ReturnObject> addShop(ShopVo shopVo) {
        ShopPo shopPo=new ShopPo(shopVo);
        shopPo.setState(1);
        return shopRepository.save(shopPo).map(Shop::new)
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> modifyShop(Long id, ShopVo shopVo) {

        return shopRepository.findById(id).defaultIfEmpty(new ShopPo())
                .flatMap(shopPo -> {
                    if(shopPo.getId()==null){
                        return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                    }else{
                        shopPo.setName(shopVo.getName());
                        shopPo.setGmtModified(LocalDateTime.now());
                        return shopRepository.save(shopPo).map(ReturnObject::new);
                    }
                });
    }

    public Mono<Object> deleteShop(Long id) {
        return shopRepository.findById(id).defaultIfEmpty(new ShopPo())
                .flatMap(shopPo -> {
                    if(shopPo.getId()==null){
                        return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }else{
                        return shopRepository.deleteById(id);
                    }
                }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> auditShop(Long shopId,Long id, AuditShopVo auditShopVo) {
        return shopRepository.findById(shopId).defaultIfEmpty(new ShopPo())
                .flatMap(shopPo -> {
                    if(shopPo.getId()==null){
                        return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }else{
                        shopPo.setState(auditShopVo.getType()?1:0);
                        return shopRepository.save(shopPo);
                    }
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> onShelvesShop(Long id) {
        return shopRepository.findById(id).defaultIfEmpty(new ShopPo()).flatMap(shopPo -> {
            if(shopPo==null){
                return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else{
                shopPo.setState(1);
                return shopRepository.save(shopPo);
            }
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> offShelvesShop(Long id) {
        return shopRepository.findById(id).defaultIfEmpty(new ShopPo()).flatMap(shopPo -> {
            if(shopPo.getId()==null){
                return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else{
                shopPo.setState(0);
                return shopRepository.save(shopPo);

            }
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> findAllShop(Integer page,Integer pageSize) {
        return shopRepository.findAll().map(Shop::new).collect(Collectors.toList())
                .map(list->commonUtil.listToPage(list,page,pageSize)).map(ReturnObject::new);
    }
}
