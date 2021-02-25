package demo.aftersale.controller;

import com.example.util.ReturnObject;
import demo.advertise.model.po.AdvertisePo;
import demo.advertise.model.vo.ModifiedAdVo;
import demo.aftersale.model.po.AfterSalePo;
import demo.aftersale.model.vo.*;
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

    /**
     * 买家查看售后单
     * TODO 解析token获得customId；
     */
    @GetMapping("/aftersales")
    public Mono getAllAfterSale(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) Integer state,
                                @RequestParam(required = false) Integer type){
        return afterSaleService.getAllAfterSale(101L,page,pageSize,state,type);
    }

    /**
     * 管理员查看售后单
     * TODO 验证管理员权限
     */
    @GetMapping("/shops/{id}/aftersales")
    public Mono getAllAfterSaleAdmin(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) Integer state,
                                @RequestParam(required = false) Integer type){
        return afterSaleService.getAllAfterSaleAdmin(page,pageSize,state,type);
    }

    /**
     * 用户通过id查找售后单
     * TODO 解析token获得customId；
     */
    @GetMapping("/aftersales/{id}")
    public Mono getById(@PathVariable Long id){
        return afterSaleService.getById(101L,id);
    }

    /**
     * 用户修改售后单
     * TODO 解析token获得customId；
     */
    @PutMapping("/aftersales/{id}")
    public Mono ModifiedById(@PathVariable Long id, @RequestBody ModifiedAfterSaleVo vo){
        return afterSaleService.ModifiedById(101L,id,vo);
    }

    /**
     * 用户删除售后单
     * TODO 解析token获得customId；
     */
    @DeleteMapping("/aftersales/{id}")
    public Mono deleteById(@PathVariable Long id){
        return afterSaleService.deleteById(101L,id);
    }


    /**
     * 用户填写运单信息
     * TODO 解析token获得customId；
     */
    @PutMapping("/aftersales/{id}/sendback")
    public Mono sendBack(@PathVariable Long id, @RequestBody SendBackVo vo){
        return afterSaleService.sendBack(101L,id,vo.getLogSn());
    }

    /**
     * 用户确认售后单结束
     * TODO 解析token获得customId；
     */
    @PutMapping("/aftersales/{id}/confirm")
    public Mono confirm(@PathVariable Long id){
        return afterSaleService.confirm(101L,id);
    }

    /**
     * 管理员通过Id查看售后单
     * TODO 校验管理员权限
     */
    @GetMapping("/shops/{shopId}/aftersales/{id}")
    public Mono getByIdAdmin(@PathVariable Long shopId,@PathVariable Long id){
        return afterSaleService.getByIdAdmin(id);
    }
    /**
     * 管理员审核售后单
     * TODO 校验管理员权限
     */
    @PutMapping("/shops/{shopId}/aftersales/{id}/confirm")
    public Mono adminConfirm(@PathVariable Long shopId, @PathVariable Long id, @RequestBody AdminConfirmVo vo){
        return afterSaleService.adminConfirm(id,vo);
    }

    /**
     * 管理员确认收到退货
     * TODO 校验管理员权限
     */
    @PutMapping("/shops/{shopId}/aftersales/{id}/receive")
    public Mono adminReceive(@PathVariable Long shopId, @PathVariable Long id, @RequestBody AdminReceiveVo vo){
        return afterSaleService.adminReceive(id,vo);
    }

    /**
     * 管理员寄出货物
     * TODO 校验管理员权限
     */
    @PutMapping("/shops/{shopId}/aftersales/{id}/deliver")
    public Mono adminReceive(@PathVariable Long shopId, @PathVariable Long id, @RequestBody AdminDeliverVo vo){
        return afterSaleService.adminDeliver(id,vo);
    }
}
