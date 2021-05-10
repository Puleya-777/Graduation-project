package com.order.service;

import com.example.util.ReturnObject;
import com.order.dao.TimeDao;
import com.order.model.bo.Time;
import com.order.model.po.TimeSegmentPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class TimeService {
    @Autowired
    private TimeDao timeDao;

    /**
     * 平台管理员新增时间段
     */
    @Transactional(rollbackFor = Exception.class)
    public Mono<TimeSegmentPo> insertTimesegment(Time bo) {
        return timeDao.insertTimesegment(bo);
    };

    /**
     * 管理员获取时间段列表
     */
    public Mono<ReturnObject> listSelectTimesegment(Time bo, Integer page, Integer pageSize) {
        return timeDao.listSelectTimesegment(bo, page, pageSize);
    }

    /**
     * 平台管理员删除时间段
     */
    public Mono<ReturnObject> deleteTimesegment(Time bo) {
        return timeDao.deleteTimesegment(bo);
    }


}
