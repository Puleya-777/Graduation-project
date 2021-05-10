package com.example.service;

import com.example.dao.GoodsDao;
import com.example.model.bo.*;
import com.example.model.po.*;
import com.example.model.vo.CouponActivityVo;
import com.example.repository.*;
import com.example.util.*;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Resource
    CouponRepository couponRepository;
    @Resource
    CouponActivityRepository couponActivityRepository;
    @Resource
    CouponSkuRepository couponSkuRepository;
    @Resource
    SpuRepository spuRepository;
    @Resource
    ShopRepository shopRepository;
    @Resource
    CouponSpuRepository couponSpuRepository;
    @Autowired
    NacosHelp nacosHelp;
    @Autowired
    CommonUtil commonUtil;
    @Autowired
    OssFileUtil ossFileUtil;
    @Autowired
    GoodsDao goodsDao;


    public Mono<ReturnObject> addCouponActivity(Long userId, Long shopId, CouponActivityVo couponActivityVo) {
        CouponActivityPo couponActivityPo=new CouponActivityPo(shopId,couponActivityVo);
        couponActivityPo.setCreatedBy(userId);
        couponActivityPo.setModiBy(userId);
        return couponActivityRepository.save(couponActivityPo).map(CouponActivityDetail::new)
                .flatMap(this::fillCouponActivity).map(ReturnObject::new);
    }

    public Mono<ReturnObject> showOwncouponactivities(Long shopId, Integer timeline, Integer page, Integer pageSize) {
        Flux<CouponActivityPo> couponActivityPoFlux=null;
        if(shopId!=null){
            couponActivityPoFlux=couponActivityRepository.findAllByShopId(shopId);
        }else{
            couponActivityPoFlux=couponActivityRepository.findAll();
        }
        return couponActivityPoFlux.map(CouponActivity::new)
                .collect(Collectors.toList())
                .map(list->commonUtil.listToPage(list,page,pageSize))
                .map(ReturnObject::new);
//                .map(couponActivities -> {
//                    PageInfo<CouponActivity> retPage=new PageInfo<>(couponActivities);
//                    retPage.setPages(page);
//                    retPage.setPageNum(page);
//                    retPage.setPageSize(pageSize);
//                    retPage.setTotal(pageSize);
//                    return new ReturnObject(retPage);
//                });
    }

    public Mono<ReturnObject> showOwnInvalidcouponactivities(Long shopId,Integer page,Integer pageSize) {
        return couponActivityRepository.findAllByShopId(shopId)
                .filter(couponActivityPo -> couponActivityPo.getState()!=null&&couponActivityPo.getState()==1)
                .map(CouponActivityDetail::new)
                .collect(Collectors.toList())
                .map(list->commonUtil.listToPage(list,page,pageSize))
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> getCouponSpu(Long id,Integer page,Integer pageSize) {
//        return couponSpuRepository.findAllByActivityId(id).flatMap(couponSkuPo -> {
//            return skuRepository.findById(couponSkuPo.getSkuId()).defaultIfEmpty(new SkuPo());
//        }).map(Sku::new).collect(Collectors.toList())
//                .map(list->commonUtil.listToPage(list,page,pageSize)).map(ReturnObject::new);
        return couponSpuRepository.findAllByActivityId(id)
                .flatMap(couponSpuPo -> goodsDao.getSpuInfoById(couponSpuPo.getSpuId()))
                .collect(Collectors.toList())
                .map(list->commonUtil.listToPage(list,page,pageSize))
                .map(ReturnObject::new);
    }


    public Mono<ReturnObject> getCouponActivityDetails(Long shopId, Long id) {
        return couponActivityRepository.findById(id).defaultIfEmpty(new CouponActivityPo())
                .map(CouponActivityDetail::new)
                .flatMap(this::fillCouponActivity)
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> modifyActivity(Long userId, Long shopId, Long id, CouponActivityVo couponActivityVo) {
        return couponActivityRepository.findById(id).defaultIfEmpty(new CouponActivityPo())
                .flatMap(couponActivityPo -> {
                    if(couponActivityPo.getId()==null){
                        return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }else {
                        couponActivityPo.setByCouponActivityVo(couponActivityVo);
                        couponActivityPo.setModiBy(userId);
                        return couponActivityRepository.save(couponActivityPo);
                    }
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> deleteActivity(Long id) {
        return couponActivityRepository.deleteCouponActivityPoById(id).map(ReturnObject::new);
    }

    public Mono<ReturnObject> addRangeOfActivity(Long shopId, Long activityId, Long spuId) {
//        return couponSkuRepository.saveAll(skus.stream().map(skuId->{
//            CouponSkuPo couponSkuPo=new CouponSkuPo();
//            couponSkuPo.setSkuId(skuId);
//            couponSkuPo.setActivityId(activityId);
//            return couponSkuPo;
//        }).collect(Collectors.toList()))
//                .collect(Collectors.toList()).map(ReturnObject::new);
        CouponSpuPo couponSpuPo=new CouponSpuPo();
        couponSpuPo.setActivityId(activityId);
        couponSpuPo.setSpuId(spuId);
        return  couponSpuRepository.save(couponSpuPo).map(CouponSpu::new).map(ReturnObject::new);


    }

    public Mono<ReturnObject> deleteActivityRange(Long spuId) {
        return couponSpuRepository.deleteById(spuId).map(ReturnObject::new);

    }

    public Mono<ReturnObject> showCoupons(Long userId, Integer state, Integer page, Integer pageSize) {
        return couponRepository.findAllByCustomerIdAndState(userId,state)
                .map(Coupon::new)
                .flatMap(coupon -> {
                    return couponActivityRepository.findById(coupon.getActivity().getId()).map(couponActivityPo -> {
                        coupon.setActivity(new CouponActivity(couponActivityPo));
                        return coupon;
                    });
                }).collect(Collectors.toList())
                .map(list->commonUtil.listToPage(list,page,pageSize))
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> changeStateOfCouponActivity(Long id, int state) {
        return couponRepository.findById(id).defaultIfEmpty(new CouponPo()).flatMap(couponPo -> {
            if(couponPo.getId()==null){
                return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
            }else {
                couponPo.setState(state);
                return couponRepository.save(couponPo).map(ReturnObject::new);
            }
        });
    }

    public Mono<CouponActivityDetail> fillCouponActivity(CouponActivityDetail couponActivityDetail){
        couponActivityDetail.setCreatedBy(nacosHelp.findUserById(couponActivityDetail.getCreatedBy().getId()));
        couponActivityDetail.setModiBy(nacosHelp.findUserById(couponActivityDetail.getModiBy().getId()));
        return shopRepository.findById(couponActivityDetail.getShop().getId())
                .defaultIfEmpty(new ShopPo()).map(shopPo -> {
                    Shop shop=new Shop(shopPo);
                    couponActivityDetail.setShop(shop);
                    return couponActivityDetail;
        });
    }

    public Mono<ReturnObject> customerAddCoupon(Long userId, Long activityId) {
        CouponPo couponPo=new CouponPo();
        couponPo.setCustomerId(userId);
        couponPo.setActivityId(activityId);
        return couponActivityRepository.findById(activityId).defaultIfEmpty(new CouponActivityPo())
                .flatMap(couponActivityPo -> {
                    if(couponActivityPo.getId()==null){
                        return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
                    }else if(couponActivityPo.getBeginTime().isAfter(LocalDateTime.now())){
                        return Mono.just(new ReturnObject(ResponseCode.COUPON_NOTBEGIN));
                    }else if(couponActivityPo.getEndTime().isBefore(LocalDateTime.now())){
                        return Mono.just(new ReturnObject(ResponseCode.COUPON_END));
                    }else if(couponActivityPo.getQuantitiyType()==1&&couponActivityPo.getQuantity()==0){
                        return Mono.just(new ReturnObject(ResponseCode.COUPON_FINISH));
                    }
                    couponPo.setBeginTime(LocalDateTime.now());
                    couponPo.setName(couponActivityPo.getName()+"的优惠卷");
                    couponPo.setState(1);
                    if(couponActivityPo.getValidTerm()==0){
                        couponPo.setEndTime(couponActivityPo.getEndTime());
                    }else{
                        couponPo.setEndTime(couponPo.getBeginTime().plusDays(couponActivityPo.getValidTerm()));
                    }
                    couponPo.setCouponSn(LocalDateTime.now().toString()+couponActivityPo.getName());
                    if(couponActivityPo.getQuantitiyType()==1){
                        couponActivityPo.setQuantity(couponActivityPo.getQuantity()-1);
                        couponActivityRepository.save(couponActivityPo);
                    }
                    return couponRepository.save(couponPo).map(Coupon::new).map(ReturnObject::new);
        });
    }

    public Mono<Object> upCouponActivityPicture(Long id, MultipartFile file) {
        return couponActivityRepository.findById(id).flatMap(couponActivityPo -> {
            String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            Random random=new Random();
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<10;i++){
                int number=random.nextInt(62);
                sb.append(str.charAt(number));
            }
            int begin = file.getOriginalFilename().indexOf(".");
            int last = file.getOriginalFilename().length();
            sb.append(file.getOriginalFilename(), begin, last);
            String filename=sb.toString();
            try {
                return ossFileUtil.uploadAliyun(file,filename).flatMap(url->{
                    couponActivityPo.setImageUrl(url);
                    return couponActivityRepository.save(couponActivityPo).map(a->{
                        if(a!=null) {
                            return new ReturnObject<>();
                        }
                        return null;
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
