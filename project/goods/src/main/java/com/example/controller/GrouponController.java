package com.example.controller;

import com.example.annotation.LoginUser;
import com.example.model.state.CommentState;
import com.example.model.state.CommentStateVo;
import com.example.model.state.GrouponState;
import com.example.model.state.GrouponStateVo;
import com.example.model.vo.GrouponVo;
import com.example.service.GrouponService;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ResponseUtil;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GrouponController {

    @Autowired
    GrouponService grouponService;

    @GetMapping("/groupons/states")
    public Mono<Object> getGrouponState(){
        GrouponState[] states= GrouponState.class.getEnumConstants();
        List<GrouponStateVo> GrouponStateVos =new ArrayList<GrouponStateVo>();
        for(int i=0;i<states.length;i++){
            GrouponStateVos.add(new GrouponStateVo(states[i]));
        }
        return Mono.just(ResponseUtil.ok(new ReturnObject<List>(GrouponStateVos).getData()));
    }

    @GetMapping("/groupons")
    public Mono<Object> queryGroupons(@LoginUser Long userId,@RequestParam Integer timeline,
                                      @RequestParam Long spuId,@RequestParam Long shopId,
                                      @RequestParam(required = false,defaultValue = "1") Integer page,
                                      @RequestParam(required = false,defaultValue = "1") Integer pageSize){
        return grouponService.queryGroupons(spuId,shopId,timeline,page,pageSize).map(Common::getPageRetObject);
    }

    @GetMapping("/shops/{id}/groupons")
    public Mono<Object> queryGroupon(@LoginUser Long userId, @PathVariable Long id,
                                     @RequestParam(required = false) Long spuId,@RequestParam String beginTime,
                                     @RequestParam String endTime,@RequestParam Integer state,
                                     @RequestParam(required = false,defaultValue = "1") Integer page,
                                     @RequestParam(required = false,defaultValue = "1") Integer pageSize){
        return grouponService.queryGroupon(spuId,id,state,beginTime,endTime,page,pageSize).map(Common::getPageRetObject);
    }

    @PostMapping("/shops/{shopId}/spus/{id}/groupons")
    public Mono<Object> createGrouponofSPU(@LoginUser Long userId, @PathVariable Long shopId,
                                           @PathVariable Long id, @RequestBody GrouponVo grouponVo){
        return grouponService.createGrouponofSPU(shopId,id,grouponVo).map(Common::getRetObject);
    }

    @PutMapping("/shops/{shopId}/groupons/{id}")
    public Mono<Object> changeGrouponofSPU(@LoginUser Long userId,@PathVariable Long shopId,
                                           @PathVariable Long id,@RequestBody GrouponVo grouponVo){
        return grouponService.changeGrouponofSPU(shopId,id,grouponVo).map(ret-> {
            if(ret.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.fail(ret.getCode());
            }
        });
    }

    @DeleteMapping("/shops/{shopId}/groupons/{id}")
    public Mono<Object> cancelGrouponofSPU(@LoginUser Long userId,@PathVariable Long shopId,
                                           @PathVariable Long id){
        return grouponService.cancelGrouponofSPU(id).map(ret-> {
            if(ret.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.fail(ret.getCode());
            }
        });
    }

    @PutMapping("/shops/{shopId}/groupons/{id}/onshelves")
    public Mono<Object> grouponOnShelves(@LoginUser Long userId,@PathVariable Long shopId,
                                         @PathVariable Long id){
        return grouponService.changeGrouponState(id,0).map(ret-> {
            if(ret.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.fail(ret.getCode());
            }
        });
    }

    @PutMapping("/shops/{shopId}/groupons/{id}/offshelves")
    public Mono<Object> grouponOffShelves(@LoginUser Long userId,@PathVariable Long shopId,
                                          @PathVariable Long id){
        return grouponService.changeGrouponState(id,1).map(ret-> {
            if(ret.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else{
                return ResponseUtil.fail(ret.getCode());
            }
        });
    }
}
