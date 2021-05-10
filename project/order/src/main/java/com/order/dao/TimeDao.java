package com.order.dao;

import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.order.model.bo.Time;
import com.order.model.po.TimeSegmentPo;
import com.order.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TimeDao {

    @Autowired
    TimeRepository timeRepository;

    /**
     * 平台管理员新增时间段
     */
    public Mono<TimeSegmentPo> insertTimesegment(Time bo) {
        TimeSegmentPo po = bo.createPo();
        return timeRepository.save(po).map(res->res);
//        return timeRepository.findByType(bo.getType().getCode().byteValue()).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
//            if (resOptional.isPresent()) {//寻找是否有重叠的时段
//                System.out.println("dao"+1);
//                List<TimeSegmentPo> list = resOptional.get();
//                LocalDateTime current = LocalDateTime.now();
//                LocalDateTime begin = translateTime(current,bo.getBeginTime());
//                LocalDateTime end = translateTime(current,bo.getEndTime());
//                System.out.println("dao"+6);
//                for(TimeSegmentPo timePo : list){
//                    LocalDateTime poBeginTime = translateTime(current,timePo.getBeginTime());
//                    LocalDateTime poEndTime = translateTime(current,timePo.getEndTime());
//                    //判断是否重叠
//                    if(!(!begin.isBefore(poEndTime)||!end.isAfter(poBeginTime))){
//                        System.out.println("dao"+2);
//                        return Mono.just(new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT));
//                    }
//                }
//            }
//            System.out.println("dao"+3);
//            return timeRepository.save(po).map(res->new ReturnObject<>(res));
//        });
    }

    LocalDateTime translateTime(LocalDateTime current, LocalDateTime time){
        return LocalDateTime.of(current.getYear(),current.getMonth(),current.getDayOfMonth(),
                time.getHour(),time.getMinute(),time.getSecond());
    }

    /**
     * 管理员获取时间段列表
     */
    public Mono<ReturnObject> listSelectTimesegment(Time bo, Integer page, Integer pageSize) {
        TimeSegmentPo po = bo.createPo();
        PageHelper.startPage(page, pageSize);
        return timeRepository.findByType(po.getType()).collect(Collectors.toList()).map(timeSegmentPo->{
            PageInfo<TimeSegmentPo> timeSegmentPoPage = new PageInfo<>(timeSegmentPo);
            List timeBoList = Lists.transform(timeSegmentPo, Time::new);
            PageInfo retObject = new PageInfo<>(timeBoList);
            retObject.setPages(timeSegmentPoPage.getPages());
            retObject.setPageNum(timeSegmentPoPage.getPageNum());
            retObject.setPageSize(timeSegmentPoPage.getPageSize());
            retObject.setTotal(timeSegmentPoPage.getTotal());
            return new ReturnObject<>(retObject);
        });
    }

    /**
     * 平台管理员删除时间段
     */
    public Mono<ReturnObject> deleteTimesegment(Time bo) {
        return timeRepository.findById(bo.getId()).flatMap(res->Mono.just(Optional.of(res)))
                .defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            TimeSegmentPo po = bo.createPo();
            return timeRepository.deleteByIdAndType(po.getId(),po.getType()).map(ret->{
                if (ret == 0) {
                    // 删除失败
                    return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
                } else {
                    // 删除成功
                    return new ReturnObject<>(ResponseCode.OK);
                }
            });
        });
    }





}
