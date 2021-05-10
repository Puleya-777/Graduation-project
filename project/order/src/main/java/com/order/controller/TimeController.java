package com.order.controller;


import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.google.common.base.Strings;
import com.order.model.bo.Time;
import com.order.model.vo.TimeGetVo;
import com.order.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class TimeController {
    @Autowired
    private TimeService timeService;

//    @Autowired
//    private HttpServletResponse httpServletResponse;

//    @DubboReference(check = false)
//    private IAdvertiseService advertiseService;
//
//    @DubboReference(check = false)
//    private IFlashsaleService flashsaleService;


    /**
     * 平台管理员新增广告时间段
     */
    @PostMapping("/shops/{did}/advertisement/timesegments")
    public Mono postAdvertisementTimesegments(@PathVariable Integer did, @RequestBody TimeGetVo vo) {
        String begin = vo.getBeginTime();
        String end = vo.getEndTime();
        //利用正则表达式判断时间格式是否符合要求
        String regex="^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(" +
                "0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})" +
                "(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+" +
                "([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";//正则表达式
        Pattern p=Pattern.compile(regex);
        Matcher m1=p.matcher(begin);
        Matcher m2=p.matcher(end);
        if (Strings.isNullOrEmpty(begin) || Strings.isNullOrEmpty(end) || !m1.matches() || !m2.matches()) {
            return Mono.just(Common.getRetObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID)));
        }
        //构建时段对象
        Time bo = new Time(Timestamp.valueOf(begin).toLocalDateTime(), Timestamp.valueOf(end).toLocalDateTime());
        //开始时间必须早于结束时间，所以这里进行了先判断开始时间是否早于结束时间，若false，需要报错
        if(!bo.getBeginTime().isBefore(bo.getEndTime())){
//            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Mono.just(Common.getRetObject(new ReturnObject<>(ResponseCode.Log_Bigger)));
        }
        //设置插入时段类型为秒杀时间段
        bo.setType(Time.Type.ADVERTISE);
        bo.setGmtCreate(LocalDateTime.now());
        return timeService.insertTimesegment(bo).map(returnObject->{
            System.out.println("controller");
            bo.setId(returnObject.getId());
            return Common.getRetObject(new ReturnObject<>(bo));
        });
    }

    /**
     * 管理员获取广告时间段列表
     */
    @GetMapping("/shops/{did}/advertisement/timesegments")
    public Mono getAdvertisementTimesegments(
            @PathVariable("did") Long did,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Time bo = new Time();
        bo.setType(Time.Type.ADVERTISE);
        return timeService.listSelectTimesegment(bo, page, pageSize);

    }

    /**
     * 平台管理员新增秒杀时间段
     */
    @PostMapping("/shops/{did}/flashsale/timesegments")
    public Mono postFlashsaleTimesegments(@PathVariable Integer did, @RequestBody TimeGetVo vo) {
        String begin =vo.getBeginTime();
        String end = vo.getEndTime();

        //利用正则表达式判断时间格式是否符合要求
        String regex="^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(" +
                "0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})" +
                "(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+" +
                "([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";//正则表达式

        Pattern p=Pattern.compile(regex);
        Matcher m1=p.matcher(begin);
        Matcher m2=p.matcher(end);
        if (Strings.isNullOrEmpty(begin) || Strings.isNullOrEmpty(end) || !m1.matches() || !m2.matches()) {
            return Mono.just(Common.getRetObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID)));
        }
        //构建时段对象
        Time bo = new Time(Timestamp.valueOf(begin).toLocalDateTime(), Timestamp.valueOf(end).toLocalDateTime());
        //开始时间必须早于结束时间，所以这里进行了先判断开始时间是否早于结束时间，若false，需要报错
        if(!bo.getBeginTime().isBefore(bo.getEndTime())){
//            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Mono.just(Common.getRetObject(new ReturnObject<>(ResponseCode.Log_Bigger)));
        }
        //设置插入时段类型为秒杀时间段
        bo.setType(Time.Type.FLASHSALE);
        bo.setGmtCreate(LocalDateTime.now());
        return timeService.insertTimesegment(bo).map(returnObject->{
            bo.setId(returnObject.getId());
            return Common.getRetObject(new ReturnObject<>(bo));
        });

    }

    /**
     * 管理员获取秒杀时间段列表
     */
    @GetMapping("/shops/{did}/flashsale/timesegments")
    public Mono getFlashsaleTimesegments(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Time bo = new Time();
        bo.setType(Time.Type.FLASHSALE);
        return timeService.listSelectTimesegment(bo, page, pageSize).map(Common::getPageRetObject);

    }

    /**
     * 平台管理员删除广告时间段
     */
    @DeleteMapping("/shops/{did}/advertisement/timesegments/{id}")
    public Mono deleteAdvertisementTimesegmentsId(@PathVariable Integer did,
                                                    @PathVariable("id") Long id) {
        Time bo = new Time();
        bo.setId(id);
        bo.setType(Time.Type.ADVERTISE);
        return timeService.deleteTimesegment(bo).map(returnObject->{
//            //删除时段下的广告
//            if (returnObject.getCode().equals(ResponseCode.OK)) {
//                advertiseService.deleteTimeSegmentAdvertisements(id);
//            }
            return Common.getRetObject(returnObject);
        });

    }



    /**
     * 平台管理员删除秒杀时间段
     */
    @DeleteMapping("/shops/{did}/flashsale/timesegments/{id}")
    public Mono deleteFlashsaleTimesegmentsId(@PathVariable Integer did, @PathVariable("id") Long id) {
        Time bo = new Time();
        bo.setId(id);
        bo.setType(Time.Type.FLASHSALE);
        return timeService.deleteTimesegment(bo).map(returnObject->{
//            //删除时段下的秒杀
//            if(returnObject.getCode().equals(ResponseCode.OK)){
//                flashsaleService.deleteSegmentFlashsale(id);
//            }
            return Common.getRetObject(returnObject);
        });
    }


}
