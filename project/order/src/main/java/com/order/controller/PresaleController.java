package com.order.controller;


import com.order.model.Status;
import com.order.model.StatusWrap;
import com.order.model.bo.PresaleActivity;
import com.order.model.vo.PresaleActivityInVo;
import com.order.model.vo.PresaleActivityModifyVo;
import com.order.model.vo.PresaleActivityVo;
import com.order.service.PresaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class PresaleController {
    @Autowired
    private PresaleService presaleService;

    /**
     *获得预售活动的所有状态
     */
    @GetMapping("/presales/states")
    public Mono getPresaleActivityStates() {
        return Mono.just(StatusWrap.of(Arrays.asList(PresaleActivity.State.values())));
    }

    /**
     *查询所有有效的预售活动
     */
    @GetMapping("/presales")
    public Mono getPresaleActivity(
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Integer timeline,
            @RequestParam Long skuId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PresaleActivityInVo presaleActivityInVo = new PresaleActivityInVo(shopId, skuId, null, timeline, page, pageSize);
        return presaleService.getPresaleActivity(presaleActivityInVo);
    }

    /**
     *管理员查询SPU所有预售活动
     */
    @GetMapping("/shops/{shopId}/presales")
    public Mono getallPresaleActivity(
            @PathVariable Long shopId,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) Integer state,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PresaleActivityInVo presaleActivityInVo = new PresaleActivityInVo(shopId, skuId, state, null, page, pageSize);
        return presaleService.getallPresaleActivity(presaleActivityInVo);
    }

    /**
     *管理员新增SKU预售活动
     */
    @PostMapping("/shops/{shopId}/skus/{id}/presales")
    public Mono createPresaleActivity(
            @PathVariable Long shopId,
            @PathVariable Long id,
            @RequestBody PresaleActivityVo vo) {
        if (vo.getName() == null || vo.getName().isEmpty() || vo.getName().isBlank())
            return Mono.just(StatusWrap.just(Status.FIELD_NOTVALID));
        vo.setState(PresaleActivity.State.OFFLINE.getCode().byteValue());
        return presaleService.createPresaleActivity(shopId, id, vo);
    }

    /**
     *管理员修改SKU预售活动
     */
    @PutMapping("/shops/{shopId}/presales/{id}")
    public Mono modifyPresaleActivity(
            @PathVariable Long shopId, @PathVariable Long id, @RequestBody PresaleActivityModifyVo vo) {
        if (vo.getName() == null || vo.getName().isEmpty() || vo.getName().isBlank())
            return Mono.just(StatusWrap.just(Status.FIELD_NOTVALID));
        vo.setShopId(shopId);
        return presaleService.modifyPresaleActivityById(id, vo);
    }

    /**
     *管理员逻辑删除SKU预售活动
     */
    @DeleteMapping("/shops/{shopId}/presales/{id}")
    public Mono deletePresaleActivity(@PathVariable Long shopId, @PathVariable Long id) {
        return presaleService.deletePresaleActivity(shopId, id);
    }

    /**
     *管理员上线预售活动
     */
    @PutMapping("/shops/{shopId}/presales/{id}/onshelves")
    public Mono ONLINEPresaleActivity(@PathVariable Long shopId, @PathVariable Long id) {
        return presaleService.PtoONLINE(shopId, id);
    }

    /**
     *管理员下线预售活动
     */
    @PutMapping("/shops/{shopId}/presales/{id}/offshelves")
    public Mono OFFLINEPresaleActivity(@PathVariable Long shopId, @PathVariable Long id) {
        return presaleService.PtoOFFLINE(shopId, id);
    }



}
