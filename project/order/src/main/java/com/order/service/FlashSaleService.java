package com.order.service;

import com.order.dao.FlashSaleDao;
import com.order.dao.GoodsSkuDao;
import com.order.model.Status;
import com.order.model.StatusWrap;
import com.order.model.bo.FlashSale;
import com.order.model.bo.FlashSaleItemExtendedView;
import com.order.model.bo.FlashSaleWithTimeSegmentView;
import com.order.model.po.GoodsSkuPo;
import com.order.model.vo.FlashSaleCreatorValidation;
import com.order.model.vo.FlashSaleItemCreatorValidation;
import com.order.model.vo.FlashSaleModifierValidation;
import com.order.feign.TimeFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FlashSaleService {

    @Autowired
    private FlashSaleDao flashSaleDao;
    @Autowired
    private GoodsSkuDao goodsSkuDao;

    @Autowired
    private TimeFeign timeFeign;

    private static final Logger logger = LoggerFactory.getLogger(FlashSaleService.class);

    /**
     *查询某一时段秒杀活动详情
     */
    public Mono<List<FlashSaleItemExtendedView>> getFlashSaleItemsWithinTimeSegment(Long id) {
//        if (!timeSegmentService.timeSegIsFlashSale(id)) return null;
        return flashSaleDao.getAllFlashSaleItemsWithinTimeSegments(Collections.singletonList(id));
    }

    /**
     *平台管理员在某个时段下新建秒杀
     */
    public Mono<ResponseEntity<StatusWrap>> createWithinTimeSegment(FlashSaleCreatorValidation vo, Long timeSegId) {
        if (vo.getFlashDate() == null
                || Timestamp.valueOf(vo.getFlashDate()).toLocalDateTime().isBefore(LocalDate.now().atTime(LocalTime.MAX))) {
            return Mono.just(StatusWrap.just(Status.FIELD_NOTVALID));
        }
        FlashSale create = new FlashSale(vo);
        create.setTimeSegId(timeSegId);
        create.setState(FlashSale.State.OFFLINE);
        create.setGmtCreate(LocalDateTime.now());
        return Mono.zip(timeFeign.getFlashsaleTimesegments(timeSegId),flashSaleDao.createActivity(create)).map(tuple->{
            if (tuple.getT1() == null) {
                logger.error("timeSeg id invalid");
                return StatusWrap.just(Status.RESOURCE_ID_NOTEXIST);
            }
            if (tuple.getT2() == null)
                return StatusWrap.just(Status.INTERNAL_SERVER_ERR);
            return StatusWrap.of(new FlashSaleWithTimeSegmentView(tuple.getT2(), tuple.getT1()), HttpStatus.CREATED);
        });
    }

    /**
     *获得当前时段秒杀列表
     */
    public Mono<List<FlashSaleItemExtendedView>> getCurrentFlashSaleItems() {
        return timeFeign.getCurrentFlashSaleTimeSegs().flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.empty();
            }
            ArrayList<Long> ids = resOptional.get();
            return flashSaleDao.getAllFlashSaleItemsWithinTimeSegments(ids);
        });
    }


    /**
     *平台管理员删除某个时段秒杀
     */
    public Mono<ResponseEntity<StatusWrap>> forceCancel(Long id) {
        return flashSaleDao.selectActivity(id).flatMap(activity->{
            if (activity == null || activity.getState() == FlashSale.State.DELETED)
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            if (activity.getState() == FlashSale.State.ONLINE)
                return Mono.just(StatusWrap.just(Status.FLASH_SALE_STATE_DENIED));
            activity.setState(FlashSale.State.DELETED);
            return flashSaleDao.updateActivity(activity).map(res->StatusWrap.ok());
        });
    }


    /**
     *管理员修改秒杀活动
     */
    public Mono<ResponseEntity<StatusWrap>> modifyInfo(Long id, FlashSaleModifierValidation vo) {
        return flashSaleDao.selectActivity(id).flatMap(origin-> {
            if (origin == null || origin.getState() == FlashSale.State.DELETED)
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            if (vo.getFlashDate() == null
                    || Timestamp.valueOf(vo.getFlashDate()).toLocalDateTime().isBefore(LocalDate.now().atTime(LocalTime.MAX))) {
                return Mono.just(StatusWrap.just(Status.FIELD_NOTVALID));
            }
            if (origin.getState() != FlashSale.State.OFFLINE)
                return Mono.just(StatusWrap.just(Status.FLASH_SALE_STATE_DENIED));
            FlashSale modified = new FlashSale(vo);
            modified.setId(id);
            modified.setGmtCreate(origin.getGmtCreate());
            modified.setTimeSegId(origin.getTimeSegId());
            modified.setState(origin.getState());
            return flashSaleDao.updateActivity(modified).map(saved->{
                if (saved == null)
                    return StatusWrap.just(Status.INTERNAL_SERVER_ERR);
                return StatusWrap.ok();
            });
        });
    }


    /**
     *管理员上线秒杀活动
     */
    public Mono<ResponseEntity<StatusWrap>> flashSaleOnline(Long id) {
        return flashSaleDao.selectActivity(id).flatMap(activity->{
            if (activity == null || activity.getState() == FlashSale.State.DELETED)
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            if (activity.getState() == FlashSale.State.ONLINE)
                return Mono.just(StatusWrap.ok());
            if (activity.getState() != FlashSale.State.OFFLINE)
                return Mono.just(StatusWrap.just(Status.FLASH_SALE_STATE_DENIED));
            activity.setState(FlashSale.State.ONLINE);
            return flashSaleDao.updateActivity(activity).map(saved->{
                if (saved == null)
                    return StatusWrap.just(Status.INTERNAL_SERVER_ERR);
                return StatusWrap.ok();
            });
        });
    }

    /**
     *管理员下线秒杀活动
     */
    public Mono<ResponseEntity<StatusWrap>> flashSaleOffline(Long id) {
        return flashSaleDao.selectActivity(id).flatMap(activity->{
            if (activity == null || activity.getState() == FlashSale.State.DELETED)
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            if (activity.getState() == FlashSale.State.OFFLINE)
                return Mono.just(StatusWrap.ok());
            if (activity.getState() != FlashSale.State.ONLINE)
                return Mono.just(StatusWrap.just(Status.FLASH_SALE_STATE_DENIED));
            activity.setState(FlashSale.State.OFFLINE);
            return flashSaleDao.updateActivity(activity).map(saved->{
                if (saved == null)
                    return StatusWrap.just(Status.INTERNAL_SERVER_ERR);
                return StatusWrap.ok();
            });
        });
    }


    /**
     *平台管理员向秒杀活动添加商品SKU
     */
    public Mono<ResponseEntity<StatusWrap>> insertItem(Long saleId, FlashSaleItemCreatorValidation vo) {
        if (vo.getSkuId() == null || vo.getPrice() == null || vo.getQuantity() == null)
            return Mono.just(StatusWrap.just(Status.FIELD_NOTVALID));
        return Mono.zip(flashSaleDao.selectActivity(saleId),goodsSkuDao.getSkuPoById(vo.getSkuId())).flatMap(tuple->{
            FlashSale sale = tuple.getT1();
            GoodsSkuPo sku = tuple.getT2();
            if (sale == null || sku == null)
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            FlashSale.Item create = new FlashSale.Item(vo);
            create.setSaleId(saleId);
            create.setGmtCreate(LocalDateTime.now());
            return Mono.zip(flashSaleDao.insertItem(create),goodsSkuDao.getSingleSimpleSku(vo.getSkuId().intValue())).map(tuple2->{
                if (tuple2.getT1() == null)
                    return StatusWrap.just(Status.INTERNAL_SERVER_ERR);
                return StatusWrap.of(new FlashSaleItemExtendedView(tuple2.getT1(), tuple2.getT2()), HttpStatus.CREATED);
            });
        });
    }

    /**
     *平台管理员在秒杀活动删除商品SKU
     */
    public Mono<ResponseEntity<StatusWrap>> removeItem(Long id) {
        return flashSaleDao.selectItem(id).flatMap(item->{
            if (item == null)
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            return flashSaleDao.selectActivity(item.getSaleId()).flatMap(parent->{
                if (parent == null)
                    return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
                if (parent.getState() == FlashSale.State.DELETED)
                    return Mono.just(StatusWrap.just(Status.FLASH_SALE_STATE_DENIED));
                return flashSaleDao.deleteItem(id).map(deleted->{
                    if (deleted == null)
                        return StatusWrap.just(Status.INTERNAL_SERVER_ERR);
                    return StatusWrap.ok();
                });
            });
        });
    }

}
