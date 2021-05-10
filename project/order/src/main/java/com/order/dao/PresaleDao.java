package com.order.dao;

import com.github.pagehelper.PageHelper;
import com.order.model.Status;
import com.order.model.StatusWrap;
import com.order.model.bo.PresaleActivity;
import com.order.model.po.PresaleActivityPo;
import com.order.model.vo.PresaleActivityInVo;
import com.order.model.vo.PresaleActivityModifyVo;
import com.order.repository.PresaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PresaleDao {
    @Autowired
    PresaleRepository presaleRepository;

//    @Autowired
//    ShopDao shopDao;
//
//    @Autowired
//    GoodsSkuDao goodsSkuDao;
//
//    @Autowired
//    GoodsSpuRepository goodsSpuRepository;
//
//    @Autowired
//    GoodsSkuRepository goodsSkuRepository;

    /**
     *查询所有有效的预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> getPresaleActivity(PresaleActivityInVo vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());

        return Mono.empty();
//        PageHelper.startPage(vo.getPage(), vo.getPageSize());
//        Mono<List<PresaleActivityPo>> presaleActivityList = presaleRepository.findByStateAndShopIdAndGoodsSKUId((byte)0,vo.getShopid(),vo.getGoodsSkuId()).collect(Collectors.toList());
//        Mono<List<PresaleActivityCreateVo>> presaleActivities = presaleActivityList.flatMap(poList->{
//            return poList.stream().map(x->{
//                return Mono.zip(shopDao.select(x.getShopId()),goodsSkuDao.getSingleSimpleSku(x.getGoodsSkuId().intValue())).flatMap(tuple->{
//                    return new PresaleActivityCreateVo(tuple.getT1(), tuple.getT2(), x);
//                });
//            }).collect(Collectors.toList());
//        });
//        PageInfo<PresaleActivityPo> presaleActivityPageInfo = presaleActivities.map(PageInfo::of);
//        return Mono.zip(presaleActivityPageInfo, presaleActivities).map(tuple->StatusWrap.of(PageWrap.of(tuple.getT1(), tuple.getT2())));
    }

    /**
     *管理员查询SPU所有预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> getallPresaleAcitvity(PresaleActivityInVo vo) {
        return Mono.empty();
//        PageHelper.startPage(vo.getPage(), vo.getPageSize());
//        Mono<List<PresaleActivityPo>> presaleActivityList = presaleRepository.findByStateAndShopIdAndGoodsSKUId((byte)0,vo.getShopid(),vo.getGoodsSkuId()).collect(Collectors.toList());
//        Mono<List<PresaleActivityCreateVo>> presaleActivities = presaleActivityList.flatMap(poList->{
//            return poList.stream().map(x->{
//                return Mono.zip(shopDao.select(x.getShopId()),goodsSkuDao.getSingleSimpleSku(x.getGoodsSkuId().intValue())).flatMap(tuple->{
//                    return new PresaleActivityCreateVo(tuple.getT1(), tuple.getT2(), x);
//                });
//            }).collect(Collectors.toList());
//        });
//        PageInfo<PresaleActivityPo> presaleActivityPageInfo = presaleActivities.map(PageInfo::of);
//        return Mono.zip(presaleActivityPageInfo, presaleActivities).map(tuple->StatusWrap.of(PageWrap.of(tuple.getT1(), tuple.getT2())));
    }

    /**
     *管理员新增SKU预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> createPresaleActivity(PresaleActivity presaleActivity) {
        PresaleActivityPo po = presaleActivity.getPresaleActivityPo();
        return presaleRepository.save(po).map(res->StatusWrap.of(po, HttpStatus.CREATED));
    }

    /**
     *管理员修改SKU预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> modifyPresaleActivity(Long id, PresaleActivityModifyVo vo) {
        return presaleRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            }
            PresaleActivityPo po = resOptional.get();
            if (po.getState() != PresaleActivity.State.OFFLINE.getCode().byteValue()) {
                return Mono.just(StatusWrap.just(Status.PRESALE_STATENOTALLOW));
            }
            if (po.getShopId() != vo.getShopId() && vo.getShopId() != 0) {
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_OUTSCOPE));
            }
            if (Timestamp.valueOf(vo.getBeginTime()).toLocalDateTime().isBefore(LocalDateTime.now())
                    || Timestamp.valueOf(vo.getPayTime()).toLocalDateTime().isBefore(LocalDateTime.now())
                    || Timestamp.valueOf(vo.getEndTime()).toLocalDateTime().isBefore(LocalDateTime.now())
                    || vo.getQuantity() < 0
                    || vo.getAdvancePayPrice() < 0
                    || vo.getRestPayPrice() < 0
            ) {
                return Mono.just(StatusWrap.just(Status.FIELD_NOTVALID));
            }
            po.setQuantity(vo.getQuantity());
            po.setAdvancePayPrice(vo.getAdvancePayPrice());
            po.setRestPayPrice(vo.getRestPayPrice());
            po.setName(vo.getName());
            po.setBeginTime(Timestamp.valueOf(vo.getBeginTime()).toLocalDateTime());
            po.setPayTime(Timestamp.valueOf(vo.getPayTime()).toLocalDateTime());
            po.setEndTime(Timestamp.valueOf(vo.getEndTime()).toLocalDateTime());
            po.setGmtModified(LocalDateTime.now());
            return presaleRepository.save(po).map(res->StatusWrap.just(Status.OK));
        });
    }

    /**
     *管理员逻辑删除SKU预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> deletePresaleActivityById(Long shopId, Long id) {
        return presaleRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            }
            PresaleActivityPo po = resOptional.get();
            if (po.getShopId() != shopId && shopId != 0) {
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_OUTSCOPE));
            }
            if (po.getState() != PresaleActivity.State.OFFLINE.getCode().byteValue()) {
                return Mono.just(StatusWrap.just(Status.PRESALE_STATENOTALLOW));
            }
            po.setGmtModified(LocalDateTime.now());
            po.setState(PresaleActivity.State.DELETE.getCode());
            return presaleRepository.save(po).map(res->StatusWrap.just(Status.OK));
        });
    }

    /**
     *管理员上线预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> PtoONLINE(Long shopId, Long id) {
        return presaleRepository.findById(id).flatMap(res->Mono.just(Optional.of(res)))
        .defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            }
            PresaleActivityPo po = resOptional.get();
            if (po.getShopId() != shopId && shopId != 0) {
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_OUTSCOPE));
            }
            if (po.getState() != PresaleActivity.State.OFFLINE.getCode().byteValue()) {
                return Mono.just(StatusWrap.just(Status.PRESALE_STATENOTALLOW));
            }
            po.setGmtModified(LocalDateTime.now());
            po.setState(PresaleActivity.State.ONLINE.getCode());
            return presaleRepository.save(po).map(res->StatusWrap.just(Status.OK));
        });
    }

    /**
     *管理员下线预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> PtoOFFLINE(Long shopId, Long id) {
        return presaleRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            }
            PresaleActivityPo po = resOptional.get();
            if (po.getShopId() != shopId && shopId != 0) {
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_OUTSCOPE));
            }
            if (po.getState() != PresaleActivity.State.ONLINE.getCode().byteValue()) {
                return Mono.just(StatusWrap.just(Status.PRESALE_STATENOTALLOW));
            }
            po.setGmtModified(LocalDateTime.now());
            po.setState(PresaleActivity.State.OFFLINE.getCode());
            return presaleRepository.save(po).map(res->StatusWrap.just(Status.OK));
        });
    }

}
