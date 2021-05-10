package com.order.dao;

import com.order.model.po.GoodsSkuPo;
import com.order.model.vo.ReturnGoodsSkuVo;
import com.order.repository.GoodsSkuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class GoodsSkuDao {
    @Autowired
    GoodsSkuRepository goodsSKURepository;

    public Mono<ReturnGoodsSkuVo> getSingleSimpleSku(Integer id) {
        return goodsSKURepository.findById(id.longValue()).map(po->{
            if (po == null || po.getDisabled() != (byte) 0 || po.getState() != (byte) 4) return null;
            //return new ReturnGoodsSkuVo(po, selectFloatPrice(selectFloatPrice(po.getId())))
            return new ReturnGoodsSkuVo(po, (long)1);
        });
    }

    public Mono<GoodsSkuPo> getSkuPoById(Long id){
        return goodsSKURepository.findById(id);
    }

}
