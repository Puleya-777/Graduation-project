package demo.aftersale.controller;

import com.example.annotation.Audit;
import com.example.annotation.LoginUser;
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
     */
    @Audit
    @PostMapping("/orderitems/{id}/aftersales")
    public Mono newAfterSale(@LoginUser Long userId, @PathVariable Long id, @RequestBody NewAfterSaleVo vo){
        return afterSaleService.newAfterSale(id,userId,vo);
    }

    /**
     * 买家查看售后单
     */
    @GetMapping("/aftersales")
    @Audit
    public Mono getAllAfterSale(@LoginUser Long userId,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) Integer state,
                                @RequestParam(required = false) Integer type){
        return afterSaleService.getAllAfterSale(userId,page,pageSize,state,type);
    }

    /**
     * 管理员查看售后单
     */
    @Audit
    @GetMapping("/shops/{id}/aftersales")
    public Mono getAllAfterSaleAdmin(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) Integer state,
                                @RequestParam(required = false) Integer type){
        return afterSaleService.getAllAfterSaleAdmin(page,pageSize,state,type);
    }

    /**
     * 用户通过id查找售后单
     */
    @Audit
    @GetMapping("/aftersales/{id}")
    public Mono getById(@LoginUser Long userId,@PathVariable Long id){
        return afterSaleService.getById(userId,id);
    }

    /**
     * 用户修改售后单
     */
    @Audit
    @PutMapping("/aftersales/{id}")
    public Mono ModifiedById(@LoginUser Long userId,@PathVariable Long id, @RequestBody ModifiedAfterSaleVo vo){
        return afterSaleService.ModifiedById(userId,id,vo);
    }

    /**
     * 用户删除售后单
     */
    @Audit
    @DeleteMapping("/aftersales/{id}")
    public Mono deleteById(@LoginUser Long userId,@PathVariable Long id){
        return afterSaleService.deleteById(userId,id);
    }


    /**
     * 用户填写运单信息
     */
    @Audit
    @PutMapping("/aftersales/{id}/sendback")
    public Mono sendBack(@LoginUser Long userId,@PathVariable Long id, @RequestBody SendBackVo vo){
        return afterSaleService.sendBack(userId,id,vo.getLogSn());
    }

    /**
     * 用户确认售后单结束
     */
    @Audit
    @PutMapping("/aftersales/{id}/confirm")
    public Mono confirm(@LoginUser Long userId,@PathVariable Long id){
        return afterSaleService.confirm(101L,id);
    }

    /**
     * 管理员通过Id查看售后单
     */
    @Audit
    @GetMapping("/shops/{shopId}/aftersales/{id}")
    public Mono getByIdAdmin(@PathVariable Long shopId,@PathVariable Long id){
        return afterSaleService.getByIdAdmin(id);
    }
    /**
     * 管理员审核售后单
     */
    @Audit
    @PutMapping("/shops/{shopId}/aftersales/{id}/confirm")
    public Mono adminConfirm(@PathVariable Long shopId, @PathVariable Long id, @RequestBody AdminConfirmVo vo){
        return afterSaleService.adminConfirm(id,vo);
    }

    /**
     * 管理员确认收到退货
     */
    @Audit
    @PutMapping("/shops/{shopId}/aftersales/{id}/receive")
    public Mono adminReceive(@PathVariable Long shopId, @PathVariable Long id, @RequestBody AdminReceiveVo vo){
        return afterSaleService.adminReceive(id,vo);
    }

    /**
     * 管理员寄出货物
     */
    @Audit
    @PutMapping("/shops/{shopId}/aftersales/{id}/deliver")
    public Mono adminReceive(@PathVariable Long shopId, @PathVariable Long id, @RequestBody AdminDeliverVo vo){
        return afterSaleService.adminDeliver(id,vo);
    }
}
