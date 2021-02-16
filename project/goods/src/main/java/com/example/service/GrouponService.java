package com.example.service;

import com.example.model.po.GrouponActivityPo;
import com.example.model.vo.GrouponVo;
import com.example.repository.GrouponRepository;
import com.example.util.ReturnObject;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GrouponService {

    @Resource
    GrouponRepository grouponRepository;

    public Mono<ReturnObject> getGrouponState() {
        return grouponRepository.findAll().map(grouponActivityPo -> grouponActivityPo.getState())
                .distinct().collect(Collectors.toList()).map(ReturnObject::new);
    }

    public Mono<ReturnObject> queryGroupons(Long spuId, Long shopId, Integer timeline, Integer page, Integer pageSize) {
        return grouponRepository.findAllByGoodsSpuIdAndShopId(spuId,shopId)
                .collect(Collectors.toList()).map(ReturnObject::new);
    }

    public Mono<ReturnObject> queryGroupon(Long spuId, Long id, Integer state, String beginTime, String endTime, Integer page, Integer pageSize) {
        return grouponRepository.findAllByGoodsSpuIdAndShopId(spuId,id)
                .filter(grouponActivityPo -> grouponActivityPo.getState()==state)
                .collect(Collectors.toList())
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> createGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo) {
        return grouponRepository.save(new GrouponActivityPo(id,shopId,grouponVo))
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> changeGrouponofSPU(Long shopId, Long id, GrouponVo grouponVo) {
        return grouponRepository.findById(id).flatMap(grouponActivityPo -> {
            grouponActivityPo.setByGrouponVo(grouponVo);
            return grouponRepository.save(grouponActivityPo);
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> cancelGrouponofSPU(Long id) {
        return grouponRepository.deleteById(id).map(ReturnObject::new);
    }

    public Mono<ReturnObject> changeGrouponState(Long id, int state) {
        return grouponRepository.findById(id).flatMap(grouponActivityPo -> {
            grouponActivityPo.setState(1);
            return grouponRepository.save(grouponActivityPo);
        }).map(ReturnObject::new);
    }
}
