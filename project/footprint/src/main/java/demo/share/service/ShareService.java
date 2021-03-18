package demo.share.service;

import com.example.annotation.LoginUser;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import demo.advertise.model.po.AdvertisePo;
import demo.share.model.po.BeSharePo;
import demo.share.model.po.ShareActivityPo;
import demo.share.model.po.SharePo;
import demo.share.model.vo.NewActivityVo;
import demo.share.model.vo.ShareReturnVo;
import demo.share.repository.BeShareRepository;
import demo.share.repository.ShareActivityRepository;
import demo.share.repository.ShareRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author chei1
 */
@Service
@Slf4j
public class ShareService {
    @Resource
    ShareActivityRepository shareActivityRepository;
    @Resource
    ShareRepository shareRepository;
    @Resource
    BeShareRepository beShareRepository;

    /**
     * 分享活动
     */
    public Mono newActivity(Long shopId,Long skuId,NewActivityVo vo){
        return shareActivityRepository.countAllByGoodsSkuId(skuId).flatMap(aa->{
            if(aa==0){
                ShareActivityPo po=new ShareActivityPo();
                po.setBeginTime(LocalDateTime.parse(vo.getBeginTime()));
                po.setEndTime(LocalDateTime.parse(vo.getEndTime()));
                po.setStrategy(vo.getStrategy());
                po.setShopId(shopId);
                po.setGoodsSkuId(skuId);
                po.setQuantity(vo.getQuantity());
                po.setGmtCreate(LocalDateTime.now());
                po.setGmtModified(LocalDateTime.now());
                po.setState(1);
                return shareActivityRepository.save(po).map(it-> new ReturnObject<>(it));
            }
            return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"该商品已存在分享活动"));
        });
    }

    public Mono getActivities(Long shopId,Long skuId,Integer pageNum,Integer pageSize){
        return Mono.just("puleya").flatMap(aa->{
           if(skuId!=null&&shopId!=null){
               return shareActivityRepository.findAllByShopIdAndGoodsSkuId(shopId,skuId).collectList();
           }else if(skuId!=null){
               return shareActivityRepository.findAllByGoodsSkuId(skuId).collectList();
           }else if(shopId!=null){
               return shareActivityRepository.findAllByShopId(shopId).collectList();
           }else {
               return shareActivityRepository.findAll().collectList();
           }
        }).map(it->{
            Page page = new Page(pageNum, pageSize);
            int total = it.size();
            page.setTotal(total);
            int startIndex = Math.min((pageNum - 1) * pageSize,total);
            int endIndex = Math.min(startIndex + pageSize,total);
            page.addAll(it.subList(startIndex,endIndex));
            PageInfo<AdvertisePo> retPage=new PageInfo(page);
            return new ReturnObject(retPage);
        });
    }

    public Mono modifiedActivities(Long shopId,Long id,NewActivityVo vo){
        return shareActivityRepository.findById(id).flatMap(po->{
           if(po.getState()!=1){
               return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT));
           }
           if(!po.getShopId().equals(shopId)){
               return Mono.just(new ReturnObject<>(ResponseCode.USERPROXY_DEPART_MANAGER_CONFLICT));
           }
           po.setBeginTime(LocalDateTime.parse(vo.getBeginTime()));
           po.setEndTime(LocalDateTime.parse(vo.getEndTime()));
           po.setStrategy(vo.getStrategy());
           po.setQuantity(vo.getQuantity());
           po.setGmtModified(LocalDateTime.now());
           return shareActivityRepository.save(po).map(it-> {
               if(it!=null) {
                  return new ReturnObject<>();
               }
               return null;
           });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }
    public Mono online(Long shopId,Long id){
        return shareActivityRepository.findById(id).flatMap(po-> {
            if (po.getState() != 1) {
                return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT));
            }
            if (!po.getShopId().equals(shopId)) {
                return Mono.just(new ReturnObject<>(ResponseCode.USERPROXY_DEPART_MANAGER_CONFLICT));
            }
            po.setState(2);
            po.setGmtModified(LocalDateTime.now());
            return shareActivityRepository.save(po).map(it-> {
                if(it!=null) {
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono offline(Long shopId,Long id){
        return shareActivityRepository.findById(id).flatMap(po-> {
            if (!po.getShopId().equals(shopId)) {
                return Mono.just(new ReturnObject<>(ResponseCode.USERPROXY_DEPART_MANAGER_CONFLICT));
            }
            return shareActivityRepository.deleteShareActivityPoById(id).map(it->{
                if(it!=0){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }


    public Mono shareGoods(Long userId,Long skuId){
        return shareActivityRepository.findByGoodsSkuId(skuId).flatMap(activityPo->{
            if(activityPo.getState()!=1){
                return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"分享活动不存在"));
            }
            if(activityPo.getBeginTime().isAfter(LocalDateTime.now())){
                return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"分享活动还未开始"));
            }
            if(activityPo.getEndTime().isBefore(LocalDateTime.now())){
                return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"分享活动已经结束"));
            }
            SharePo po=new SharePo();
            po.setSharerId(userId);
            po.setGoodsSkuId(skuId);
            po.setQuantity(activityPo.getQuantity());
            po.setGmtCreate(LocalDateTime.now());
            po.setGmtModified(LocalDateTime.now());
            po.setShareActivityId(activityPo.getId());

            String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            Random random=new Random();
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<11;i++){
                int number=random.nextInt(62);
                sb.append(str.charAt(number));
            }
            po.setShareUrl(sb.toString());
            return shareRepository.save(po).map(it->{
                ShareReturnVo vo=new ShareReturnVo();
                vo.setId(it.getId());
                vo.setGmtCreate(it.getGmtCreate());
                vo.setQuantity(it.getQuantity());
                vo.setSharerId(it.getSharerId());
                vo.setShareUrl(it.getShareUrl());
                /**
                 * TODO 此处需要查详细Sku填入
                 */
                vo.setSkuId(it.getGoodsSkuId());
                return new ReturnObject(vo);
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"分享活动不存在"));
    }

    public Mono buyGoods(Long userId,Long orderId,String url){
        return shareRepository.findByShareUrl(url).flatMap(sharePo -> {
            if(sharePo.getSharerId().equals(userId)){
                return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"无法购买自己分享的商品"));
            }
            return shareActivityRepository.findById(sharePo.getShareActivityId()).flatMap(activityPo -> {
                if(activityPo.getState()!=1){
                    return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"分享活动不存在"));
                }
                if(activityPo.getBeginTime().isAfter(LocalDateTime.now())){
                    return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"分享活动还未开始"));
                }
                if(activityPo.getEndTime().isBefore(LocalDateTime.now())){
                    return Mono.just(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"分享活动已经结束"));
                }
                BeSharePo beSharePo=new BeSharePo();
                beSharePo.setCustomerId(userId);
                beSharePo.setGmtCreate(LocalDateTime.now());
                beSharePo.setGmtModified(LocalDateTime.now());
                beSharePo.setGoodsSkuId(sharePo.getGoodsSkuId());
                beSharePo.setOrderId(orderId);
                beSharePo.setRebate(sharePo.getQuantity()==null?1:sharePo.getQuantity());
                beSharePo.setShareActivityId(sharePo.getShareActivityId());
                beSharePo.setShareId(sharePo.getId());
                beSharePo.setSharerId(sharePo.getSharerId());
                return beShareRepository.save(beSharePo).map(it->{
                   if(it!=null){
                       return new ReturnObject<>(it);
                   }
                   return null;
                });
            }).defaultIfEmpty(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"分享活动不存在"));

        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.SHAREACT_CONFLICT,"无该分享记录"));
    }


    /**
     * TODO 以下四个接口均需要查详细Sku填入
     */
    public Mono getShares(Long userId,Long skuId,Integer pageNum,Integer pageSize){
        return shareRepository.findAllBySharerId(userId).collectList().map(it->{
            if(skuId!=null){
                it=it.stream().filter(sharePo -> skuId.equals(sharePo.getGoodsSkuId())).collect(Collectors.toList());
            }
            return it;
        }).map(it->{
            Page page = new Page(pageNum, pageSize);
            int total = it.size();
            page.setTotal(total);
            int startIndex = Math.min((pageNum - 1) * pageSize,total);
            int endIndex = Math.min(startIndex + pageSize,total);
            page.addAll(it.subList(startIndex,endIndex));
            PageInfo<AdvertisePo> retPage=new PageInfo(page);
            return new ReturnObject(retPage);
        });
    }
    public Mono getSharesBySku(Long skuId,Integer pageNum,Integer pageSize){
        return shareRepository.findAllByGoodsSkuId(skuId).collectList().map(it->{
            Page page = new Page(pageNum, pageSize);
            int total = it.size();
            page.setTotal(total);
            int startIndex = Math.min((pageNum - 1) * pageSize,total);
            int endIndex = Math.min(startIndex + pageSize,total);
            page.addAll(it.subList(startIndex,endIndex));
            PageInfo<AdvertisePo> retPage=new PageInfo(page);
            return new ReturnObject(retPage);
        });
    }
    public Mono getBeShare(Long userId,Long skuId,Integer pageNum,Integer pageSize){
        return beShareRepository.findAllBySharerId(userId).collectList().map(it->{
            if(skuId!=null){
                it=it.stream().filter(sharePo -> skuId.equals(sharePo.getGoodsSkuId())).collect(Collectors.toList());
            }
            return it;
        }).map(it->{
            Page page = new Page(pageNum, pageSize);
            int total = it.size();
            page.setTotal(total);
            int startIndex = Math.min((pageNum - 1) * pageSize,total);
            int endIndex = Math.min(startIndex + pageSize,total);
            page.addAll(it.subList(startIndex,endIndex));
            PageInfo<AdvertisePo> retPage=new PageInfo(page);
            return new ReturnObject(retPage);
        });
    }

    public Mono getBeSharesBySku(Long skuId,Integer pageNum,Integer pageSize){
        return beShareRepository.findAllByGoodsSkuId(skuId).collectList().map(it->{
            Page page = new Page(pageNum, pageSize);
            int total = it.size();
            page.setTotal(total);
            int startIndex = Math.min((pageNum - 1) * pageSize,total);
            int endIndex = Math.min(startIndex + pageSize,total);
            page.addAll(it.subList(startIndex,endIndex));
            PageInfo<AdvertisePo> retPage=new PageInfo(page);
            return new ReturnObject(retPage);
        });
    }
}
