package demo.share.controller;

import com.example.annotation.Audit;
import com.example.annotation.LoginUser;
import com.example.util.ReturnObject;
import demo.share.model.po.ShareActivityPo;
import demo.share.model.vo.NewActivityVo;
import demo.share.service.ShareService;
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
public class ShareController {
    @Autowired
    ShareService shareService;
    /**
     * 分享活动
     */
    @GetMapping("/shareactivities/states")
    public Mono getState(){
        List<StateVo> list=new ArrayList<>();
        for(Integer a: ShareActivityPo.stateMap.keySet()){
            list.add(new StateVo(a, ShareActivityPo.stateMap.get(a)));
        }
        return Mono.just(new ReturnObject<>(list));
    }

    @PostMapping("/shops/{shopId}/skus/{id}/shareactivities")
    @Audit
    public Mono newActivity(@PathVariable Long shopId, @PathVariable Long id, @RequestBody NewActivityVo vo){
        return shareService.newActivity(shopId,id,vo);
    }
    @GetMapping("/shareactivities")
    public Mono getActivities(@RequestParam Long shopId,@RequestParam Long skuId,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer pageSize){
        return shareService.getActivities(shopId,skuId,page,pageSize);
    }
    @PutMapping("/shops/{shopId}/shareactivities/{id}")
    @Audit
    public Mono modifiedActivities(@PathVariable Long shopId, @PathVariable Long id, @RequestBody NewActivityVo vo){
        return shareService.modifiedActivities(shopId,id,vo);
    }

    @PutMapping("/shops/{shopId}/shareactivities/{id}/online")
    @Audit
    public Mono online(@PathVariable Long shopId, @PathVariable Long id){
        return shareService.online(shopId,id);
    }

    @DeleteMapping("/shops/{shopId}/shareactivities/{id}")
    @Audit
    public Mono deleteActivities(@PathVariable Long shopId, @PathVariable Long id){
        return shareService.offline(shopId,id);
    }

    /**
     * 分享
     */
    @PostMapping("/skus/{id}/shares")
    @Audit
    public Mono shareGoods(@LoginUser Long userId,@PathVariable Long id){
        return shareService.shareGoods(userId,id);
    }
    @PostMapping("/order/{orderId}/share/{url}")
    @Audit
    public Mono buyGoods(@LoginUser Long userId,@PathVariable Long orderId,@PathVariable String url){
        return shareService.buyGoods(userId,orderId,url);
    }
    @GetMapping("/shares")
    @Audit
    public Mono getShares(@LoginUser Long userId,@RequestParam(required = false) Long skuId,
                          @RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer pageSize){
        return shareService.getShares(userId,skuId,page,pageSize);
    }
    @GetMapping("/shops/{did}/skus/{id}/shares")
    @Audit
    public Mono getSharesBySku(@PathVariable Long did,@PathVariable Long id,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize){
        return shareService.getSharesBySku(id,page,pageSize);
    }

    @GetMapping("/beshared")
    @Audit
    public Mono getBeShare(@LoginUser Long userId,@RequestParam(required = false) Long skuId,
                          @RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer pageSize){
        return shareService.getBeShare(userId,skuId,page,pageSize);
    }

    @GetMapping("/shops/{did}/skus/{id}/beshared")
    @Audit
    public Mono getBeSharesBySku(@PathVariable Long did,@PathVariable Long id,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize){
        return shareService.getBeSharesBySku(id,page,pageSize);
    }
}
