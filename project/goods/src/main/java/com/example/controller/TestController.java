package com.example.controller;

import com.example.model.bo.GoodsSpu;
import com.example.model.po.SpuPo;
import com.example.repository.SpuRepository;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @Autowired
    SpuRepository spuRepository;

    @GetMapping("test")
    public Flux<SpuPo> test(){
        return spuRepository.findAll();
    }

}
