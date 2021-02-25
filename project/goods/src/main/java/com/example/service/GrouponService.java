package com.example.service;

import com.example.model.bo.GoodsSpu;
import com.example.model.bo.Groupon;
import com.example.model.bo.GrouponDetail;
import com.example.model.bo.Shop;
import com.example.model.po.GrouponActivityPo;
import com.example.model.vo.GrouponVo;
import com.example.repository.GrouponRepository;
import com.example.repository.ShopRepository;
import com.example.repository.SpuRepository;
import com.example.util.CommonUtil;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GrouponService {

    @Resource
    GrouponRepository grouponRepository;
    @Resource
    ShopRepository shopRepository;
    @Resource
    SpuRepository spuRepository;

    @Autowired
    CommonUtil commonUtil;


    public Mono<ReturnObject> queryGroupons(Long spuId, Long shopId, Integer timeline, Integer page, Integer pageSize) {
        return grouponRepository.findAllByGoodsSpuIdAndShopId(spuId,shopId)
                .filter(grouponActivityPo -> {
                    if(timeline==0){
                        return grouponActivityPo.getBeginTime().isAfter(LocalDateTime.now());
                    }else if(timeline==1){
                        return grouponActivityPo.getBeginTime().plusDays(1).getDayOfYear()==
                                LocalDateTime.now().getDayOfYear();
                    }else if(timeline==2){
                        return grouponActivityPo.getBeginTime().isBefore(LocalDateTime.now())&&
                                grouponActivityPo.getEndTime().isAfter(LocalDateTime.now());
                    }else{
                        return grouponActivityPo.getEndTime().isBefore(LocalDateTime.now());
                    }
                }).map(Groupon::new)
                .collect(Collectors.toList())
                .map(list->commonUtil.listToPage(list,page,pageSize)).map(ReturnObject::new);
    }

    public Mono<ReturnObject> queryGroupon(Long spuId, Long id, Integer state, String beginTime, String endTime, Integer page, Integer pageSize) {
        DateTimeFormatter df=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime begin=LocalDateTime.parse(beginTime,df);
        LocalDateTime end=LocalDateTime.parse(endTime,df);
        Flux<GrouponActivityPo> grouponActivityPoFlux;
        if(spuId==null){
            grouponActivityPoFlux=grouponRepository.findAllByShopId(id);
        }else{
            grouponActivityPoFlux=grouponRepository.findAllByGoodsSpuIdAndShopId(spuId,id);
        }
        return grouponActivityPoFlux
                .filter(grouponActivityPo -> grouponActivityPo.getState()==state
                            &&grouponActivityPo.getEndTime().isBefore(end)
                            &&grouponActivityPo.getBeginTime().isAfter(begin))
                .map(Groupon::new)
                .collect(Collectors.toList())
                .map(list->commonUtil.listToPage(list,page,pageSize))
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> createGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo) {
        Mono<GrouponActivityPo> grouponActivityPoMono=grouponRepository.save(new GrouponActivityPo(id,shopId,grouponVo));
        Mono<Shop> shopMono=grouponActivityPoMono.flatMap(grouponActivityPo ->
                shopRepository.findById(grouponActivityPo.getShopId()).map(Shop::new));
        Mono<GoodsSpu> goodsSpuMono=grouponActivityPoMono.flatMap(grouponActivityPo ->
                spuRepository.findById(grouponActivityPo.getGoodsSpuId()).map(GoodsSpu::new));
        return Mono.zip(grouponActivityPoMono,shopMono,goodsSpuMono).map(tuple->{
            GrouponDetail grouponDetail=new GrouponDetail(tuple.getT1());
            grouponDetail.setShop(tuple.getT2());
            grouponDetail.setGoodsSpu(tuple.getT3());
            return grouponDetail;
        }).map(ReturnObject::new);
//        return grouponRepository.save(new GrouponActivityPo(id,shopId,grouponVo))
//                .map(GrouponDetail::new)
//                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> changeGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo) {
        return grouponRepository.findById(id).defaultIfEmpty(new GrouponActivityPo())
                .flatMap(grouponActivityPo -> {
                    if(grouponActivityPo.getId()==null){
                        return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }else{
                        grouponActivityPo.setByGrouponVo(grouponVo);
                        return grouponRepository.save(grouponActivityPo);
                    }
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> cancelGrouponofSPU(Long id) {
        return grouponRepository.findById(id)
                .defaultIfEmpty(new GrouponActivityPo())
                .flatMap(grouponActivityPo -> {
                    if(grouponActivityPo.getId()==null){
                        return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                    }else{
                        return grouponRepository.deleteGrouponActivityPoById(id).map(ReturnObject::new);
                    }
                });
    }

    public Mono<ReturnObject> changeGrouponState(Long id, int state) {
        return grouponRepository.findById(id).defaultIfEmpty(new GrouponActivityPo()).flatMap(grouponActivityPo -> {
            if(grouponActivityPo.getId()==null){
                return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
            }else{
                grouponActivityPo.setState(1);
                return grouponRepository.save(grouponActivityPo);
            }
        }).map(ReturnObject::new);
    }
}
