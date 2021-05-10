package com.order.dao;

import com.order.model.bo.FlashSale;
import com.order.model.bo.FlashSaleItemExtendedView;
import com.order.model.po.FlashSaleItemPo;
import com.order.model.po.FlashSalePo;
import com.order.repository.FlashSaleItemRepository;
import com.order.repository.FlashSaleRepository;
import com.order.feign.TimeFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class FlashSaleDao {
    @Autowired
    FlashSaleRepository flashSaleRepository;

    @Autowired
    FlashSaleItemRepository flashSaleItemRepository;

    @Autowired
    TimeFeign timeFeign;

    @Autowired
    private GoodsSkuDao goodsSkuDao;

    private static final Logger logger = LoggerFactory.getLogger(FlashSaleDao.class);

    /**
     *查询某一时段秒杀活动详情
     */
    public Mono<List<FlashSaleItemExtendedView>> getAllFlashSaleItemsWithinTimeSegments(List<Long> timeSegIds) {
        return timeFeign.getRet();
//        return flashSaleRepository.findByTimeSegId(timeSegIds).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
//            if (!resOptional.isPresent()) {
//                return Mono.just(new ArrayList<FlashSaleItemPo>());
//            }
//            List<FlashSalePo> allSales = resOptional.get();
//            List<Long> saleIds = allSales.stream().map(FlashSalePo::getId).collect(Collectors.toList());
//            List<FlashSaleItemPo> items;
//            for (Long saleId:saleIds){
//                flashSaleItemRepository.findBySaleId(saleId).collect(Collectors.toList()).map(res->{
//                    return items.add(res);
//                });
//            }
//            return Mono.just(items);
//        }).flatMapMany(Flux::fromIterable).flatMap(po -> {
//            Long skuId = po.getGoodsSkuId();
//            return  goodsSkuDao.getSingleSimpleSku(skuId.intValue()).map(res->new FlashSaleItemExtendedView(new FlashSale.Item(po), res));
//        }).collect(Collectors.toList());
    }

    /**
     *平台管理员在某个时段下新建秒杀
     */
    public Mono<FlashSale> createActivity(FlashSale flashSale) {
        FlashSalePo po = flashSale.toFlashSalePo();
        return flashSaleRepository.save(po).map(res->new FlashSale(po));
    }

    /**
     *根据id查得Flashsale
     */
    public Mono<FlashSale> selectActivity(Long id) {
        return flashSaleRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return null;
            }
            return Mono.just(new FlashSale(resOptional.get()));
        });
    }

    /**
     *根据id查得FlashsaleItem
     */
    public Mono<FlashSale.Item> selectItem(Long id) {
        return flashSaleItemRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return null;
            }
            return Mono.just(new FlashSale.Item(resOptional.get()));
        });
    }

    /**
     *更新Flashsale
     */
    public Mono<FlashSale> updateActivity(FlashSale flashSale) {
        FlashSalePo flashSalePo = flashSale.toFlashSalePo();
        flashSalePo.setGmtModified(LocalDateTime.now());
        return flashSaleRepository.save(flashSalePo).map(res->flashSale);
    }

    /**
     *平台管理员向秒杀活动添加商品SKU
     */
    public Mono<FlashSale.Item> insertItem(FlashSale.Item bo) {
        FlashSaleItemPo po = bo.toItemPo();
        return flashSaleItemRepository.save(po).map(res->new FlashSale.Item(po));
    }

    /**
     *平台管理员在秒杀活动删除商品SKU
     */
    public Mono<Long> deleteItem(Long id) {
        return flashSaleItemRepository.deleteFlashSaleItemById(id).map(res->id);
    }

}
