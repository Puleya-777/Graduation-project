package com.order.feign;

import com.order.model.bo.FlashSale;
import com.order.model.bo.FlashSaleItemExtendedView;
import com.order.model.po.FlashSaleItemPo;
import com.order.model.po.GoodsSkuPo;
import com.order.model.po.TimeSegmentPo;
import com.order.model.vo.ReturnGoodsSkuVo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TimeFeign {

    public Mono<ArrayList<Long>> getCurrentFlashSaleTimeSegs(){
        ArrayList<Long> arrayList = new ArrayList<>();
        arrayList.add((long)1);
        arrayList.add((long)2);
        return Mono.just(arrayList);
    }

    public Mono<TimeSegmentPo> getFlashsaleTimesegments(Long id){
        TimeSegmentPo timeSegmentPo = new TimeSegmentPo();

        return Mono.just(timeSegmentPo);
    };

    public Mono<List<FlashSaleItemExtendedView>> getRet(){
        GoodsSkuPo goodsSkuPo = new GoodsSkuPo();
        goodsSkuPo.setId((long)1);
        goodsSkuPo.setName("花瓶");
        goodsSkuPo.setSkuSn("abcd1234");
        goodsSkuPo.setImageUrl("http");
        goodsSkuPo.setInventory(1);
        goodsSkuPo.setOriginalPrice((long)123);
        goodsSkuPo.setDisabled((byte)1);

        FlashSaleItemPo flashSaleItemPo = new FlashSaleItemPo();
        flashSaleItemPo.setSaleId((long)1);
        flashSaleItemPo.setId((long)1);
        flashSaleItemPo.setGmtCreate(LocalDateTime.now());
        flashSaleItemPo.setGmtModified(LocalDateTime.now());
        flashSaleItemPo.setQuantity(2);
        flashSaleItemPo.setPrice((long)33);
        flashSaleItemPo.setGoodsSkuId((long)127);

        FlashSale.Item f = new FlashSale.Item(flashSaleItemPo);
        
        ReturnGoodsSkuVo returnGoodsSkuVo = new ReturnGoodsSkuVo(goodsSkuPo,(long)2);
        
        FlashSaleItemExtendedView flashSaleItemExtendedView = new FlashSaleItemExtendedView(f,returnGoodsSkuVo);
        ArrayList<FlashSaleItemExtendedView> l = new ArrayList<>();
        return Mono.just(l);
    }
}
