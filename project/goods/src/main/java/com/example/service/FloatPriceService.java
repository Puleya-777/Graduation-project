package com.example.service;

import com.example.model.po.FloatPricePo;
import com.example.repository.FloatPriceRepository;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Service
public class FloatPriceService {

    @Autowired
    FloatPriceRepository floatPriceRepository;

    public Mono<ReturnObject> addFloatingPrice(FloatPricePo floatPricePo) {
        return floatPriceRepository.save(floatPricePo).defaultIfEmpty(new FloatPricePo())
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> invalidFloatPrice(Long userId, Long id) {
        return floatPriceRepository.findById(id).defaultIfEmpty(new FloatPricePo())
                .flatMap(floatPricePo -> {
                    if(floatPricePo.getId()==null){
                        return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }else{
                        return floatPriceRepository.save(floatPricePo);
                    }
                }).map(ReturnObject::new);
    }
}
