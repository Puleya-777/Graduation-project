package com.example.service;

import com.example.model.po.ShopPo;
import com.example.model.vo.AuditShopVo;
import com.example.model.vo.ShopVo;
import com.example.repository.ShopRepository;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class ShopService {

    @Autowired
    ShopRepository shopRepository;

    public Mono<ReturnObject> getshopState() {
        return shopRepository.findAll().map(shopPo -> shopPo.getState())
                .distinct().collect(Collectors.toList()).map(ReturnObject::new);
    }

    public Mono<ReturnObject> addShop(ShopVo shopVo) {
        ShopPo shopPo=new ShopPo(shopVo);
        shopPo.setState(1);
        return shopRepository.save(shopPo)
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> modifyShop(Long id, ShopVo shopVo) {
        return shopRepository.findById(id)
                .map(shopPo -> {
                    shopPo.setName(shopVo.getName());
                    shopRepository.save(shopPo);
                    return shopPo;
                }).map(ReturnObject::new);
    }

    public Mono<Object> deleteShop(Long id) {
        return shopRepository.findById(id)
                .flatMap(shopPo -> {
                    shopPo.setState(0);
                    return shopRepository.save(shopPo);
                }).map(ReturnObject::new);
    }

    public Mono<Object> auditShop(Long shopId,Long id, AuditShopVo auditShopVo) {
        return shopRepository.findById(shopId).flatMap(shopPo -> {
            shopPo.setState(auditShopVo.getType()?1:0);
            return shopRepository.save(shopPo);
        });
    }

    public Mono<ReturnObject> onShelvesShop(Long id) {
        return shopRepository.findById(id).flatMap(shopPo -> {
            shopPo.setState(1);
            return shopRepository.save(shopPo);
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> offShelvesShop(Long id) {
        return shopRepository.findById(id).flatMap(shopPo -> {
            shopPo.setState(0);
            return shopRepository.save(shopPo);
        }).map(ReturnObject::new);
    }
}
