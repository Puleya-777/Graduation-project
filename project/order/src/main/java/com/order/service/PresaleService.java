package com.order.service;

import com.order.dao.GoodsSkuDao;
import com.order.dao.PresaleDao;
import com.order.dao.ShopDao;
import com.order.model.Status;
import com.order.model.StatusWrap;
import com.order.model.bo.PresaleActivity;
import com.order.model.vo.PresaleActivityInVo;
import com.order.model.vo.PresaleActivityModifyVo;
import com.order.model.vo.PresaleActivityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class PresaleService {
    @Autowired
    private PresaleDao presaleDao;

    @Autowired
    ShopDao shopDao;

    @Autowired
    GoodsSkuDao goodsSkuDao;

    /**
     *查询所有有效的预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> getPresaleActivity(PresaleActivityInVo vo) {
        return presaleDao.getPresaleActivity(vo);
    }

    /**
     *管理员查询SPU所有预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> getallPresaleActivity(PresaleActivityInVo vo) {
        return presaleDao.getallPresaleAcitvity(vo);
    }

    /**
     *管理员新增SKU预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> createPresaleActivity(Long shopid, Long id, PresaleActivityVo vo) {
        if (vo.getName() == null || vo.getName().isEmpty() || vo.getName().isBlank())
            return Mono.just(StatusWrap.just(Status.FIELD_NOTVALID));
        if (Timestamp.valueOf(vo.getBeginTime()).toLocalDateTime().isBefore(LocalDateTime.now())
                || Timestamp.valueOf(vo.getPayTime()).toLocalDateTime().isBefore(LocalDateTime.now())
                || Timestamp.valueOf(vo.getEndTime()).toLocalDateTime().isBefore(LocalDateTime.now())
                || vo.getQuantity() < 0
                || vo.getAdvancePayPrice() < 0
                || vo.getRestPayPrice() < 0
        ) {
            return Mono.just(StatusWrap.just(Status.FIELD_NOTVALID));
        }
        return Mono.zip(shopDao.select(shopid),goodsSkuDao.getSingleSimpleSku(id.intValue())).flatMap(tuple->{
            if (tuple.getT2() == null) {
                return Mono.just(StatusWrap.just(Status.RESOURCE_ID_NOTEXIST));
            }
            PresaleActivity presaleActivity = new PresaleActivity(tuple.getT1(), tuple.getT2(), vo);
            return presaleDao.createPresaleActivity(presaleActivity);
        });
    }

    /**
     *管理员修改SKU预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> modifyPresaleActivityById(Long id, PresaleActivityModifyVo vo) {
        return presaleDao.modifyPresaleActivity(id, vo);
    }

    /**
     *管理员逻辑删除SKU预售活动
     */
    public Mono<ResponseEntity<StatusWrap>> deletePresaleActivity(Long shopId, Long id) {
        return presaleDao.deletePresaleActivityById(shopId, id);
    }

    /**
     *管理员上线预售活动
     */
    public Mono<ResponseEntity<StatusWrap>>PtoONLINE(Long shopId, Long Id) {
        return presaleDao.PtoONLINE(shopId, Id);
    }


    /**
     *管理员下线预售活动
     */
    public Mono<ResponseEntity<StatusWrap>>PtoOFFLINE(Long shopId, Long Id) {
        return presaleDao.PtoOFFLINE(shopId, Id);
    }
}
