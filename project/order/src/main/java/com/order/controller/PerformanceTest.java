package com.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class PerformanceTest {
    @Autowired
    FreightController freightController;

    @GetMapping("/webflux")
    public Mono<List<Integer>> performanceTestWebflux(){
        List<Integer> list  = IntStream.range(1, 50).boxed().collect(Collectors.toList());
        return freightController.performanceTestWebflux(list);
    }

    @GetMapping("/normal")
    public List<Integer> performanceTestNormal(){
        List<Integer> list  = IntStream.range(1, 50).boxed().collect(Collectors.toList());
        return freightController.performanceTestNormal(list);
    }
}
