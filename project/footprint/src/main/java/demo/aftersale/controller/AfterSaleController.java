package demo.aftersale.controller;

import com.example.util.ReturnObject;
import demo.advertise.model.po.AdvertisePo;
import demo.aftersale.model.po.AfterSalePo;
import demo.aftersale.model.vo.NewAfterSaleVo;
import demo.aftersale.service.AfterSaleService;
import demo.util.StateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chei1
 */
@RestController
@Slf4j
public class AfterSaleController {

    @Autowired
    AfterSaleService afterSaleService;

    @GetMapping("/aftersales/states")
    public Mono getAllState(){
        List<StateVo> list=new ArrayList<>();
        for(Integer a: AfterSalePo.stateMap.keySet()){
            list.add(new StateVo(a,AfterSalePo.stateMap.get(a)));
        }
        return Mono.just(new ReturnObject<>(list));
    }

    /**
     *  买家提交售后单
     *  TODO 解析token获得customId；
     */
    @PostMapping("/orderitems/{id}/aftersales")
    public Mono newAfterSale(@PathVariable Long id, @RequestBody NewAfterSaleVo vo){
        return afterSaleService.newAfterSale(id,101L,vo);
    }
}
