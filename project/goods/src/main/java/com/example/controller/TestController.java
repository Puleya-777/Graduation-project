package com.example.controller;

import com.example.util.ReturnObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {
    
    @GetMapping("test")
    public Mono test(){
        return Mono.just(new ReturnObject<>());
    }

}
