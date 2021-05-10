package com.order.controller;

import com.order.model.vo.FlashSaleCreatorValidation;
import com.order.model.vo.FlashSaleItemCreatorValidation;
import com.order.model.vo.FlashSaleModifierValidation;
import com.order.service.FlashSaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class FlashSaleController {
    private static final Logger logger = LoggerFactory.getLogger(FlashSaleController.class);

    @Autowired
    private FlashSaleService flashSaleService;

    /**
     *查询某一时段秒杀活动详情
     */
    @GetMapping("/timesegments/{id}/flashsales")
    public Mono getFlashSaleItemsWithinTimeSegment(@PathVariable Long id) {
        return flashSaleService.getFlashSaleItemsWithinTimeSegment(id);
    }

    /**
     *平台管理员在某个时段下新建秒杀
     */
    @PostMapping("/shops/{did}/timesegments/{id}/flashsales")
    public Mono createFlashSale(@PathVariable Long did,
                                @PathVariable Long id,
                                @RequestBody FlashSaleCreatorValidation flashSaleVo) {
        return flashSaleService.createWithinTimeSegment(flashSaleVo, id);
    }

    /**
     *获得当前时段秒杀列表
     */
    @GetMapping("/flashsales/current")
    public Mono getCurrentFlashSaleItems() {
        return flashSaleService.getCurrentFlashSaleItems();
    }

    /**
     *平台管理员删除某个时段秒杀
     */
    @DeleteMapping("/shops/{did}/flashsales/{id}")
    public Mono cancelFlashSale(
            @PathVariable Long did,
            @PathVariable Long id) {
        return flashSaleService.forceCancel(id);
    }

    /**
     *管理员修改秒杀活动
     */
    @PutMapping("/shops/{did}/flashsales/{id}")
    public Mono modifyFlashSaleInfo(
            @PathVariable Long did,
            @PathVariable Long id,
            @RequestBody FlashSaleModifierValidation FlashSaleVo) {
        return flashSaleService.modifyInfo(id, FlashSaleVo);
    }

    /**
     *管理员上线秒杀活动
     */
    @PutMapping("/shops/{did}/flashsales/{id}/onshelves")
    public Mono flashSaleOnline(
            @PathVariable Long did,
            @PathVariable Long id) {
        return flashSaleService.flashSaleOnline(id);
    }


    /**
     *管理员下线秒杀活动
     */
    @PutMapping("/shops/{did}/flashsales/{id}/offshelves")
    public Mono bringFlashSaleOffline(
            @PathVariable Long did,
            @PathVariable Long id) {
        return flashSaleService.flashSaleOffline(id);
    }

    /**
     *平台管理员向秒杀活动添加商品SKU
     */
    @PostMapping("/shops/{did}/flashsales/{id}/flashitems")
    public Mono addItemToFlashSale(
            @PathVariable Long did,
            @PathVariable Long id,
            @RequestBody FlashSaleItemCreatorValidation itemVo) {
        return flashSaleService.insertItem(id, itemVo);
    }

    /**
     *平台管理员在秒杀活动删除商品SKU
     */
    @DeleteMapping("/shops/{did}/flashsales/{fid}/flashitems/{id}")
    public Mono removeFlashSaleItemById(
            @PathVariable Long did,
            @PathVariable Long fid,
            @PathVariable Long id) {
        return flashSaleService.removeItem(id);
    }


}
