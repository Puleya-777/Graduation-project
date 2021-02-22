package demo.advertise.service;

import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import demo.util.OssFileUtil;
import demo.advertise.model.po.AdvertisePo;
import demo.advertise.model.vo.ModifiedAdVo;
import demo.advertise.model.vo.NewAdVo;
import demo.advertise.repository.AdvertiseRepository;
import demo.advertise.repository.TimeSegmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * @author chei1
 */
@Slf4j
@Service
public class AdvertiseService {
    @Resource
    AdvertiseRepository advertiseRepository;
    @Autowired
    OssFileUtil ossFileUtil;
    @Resource
    TimeSegmentRepository timeSegmentRepository;

    private static Integer MAX_AD_LIMIT=8;


    public Mono setAdDefault(Long adId){
        return advertiseRepository.findById(adId).flatMap(it->{
            it.setBeDefault(true);
            it.setGmtModified(LocalDateTime.now());
            return advertiseRepository.save(it).map(a->{
                if(a!=null) {
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));

    }

    public Mono modifiedAd(Long adId, ModifiedAdVo vo){
        return advertiseRepository.findById(adId).flatMap(adPo->{
            adPo.trans(vo);
            return advertiseRepository.save(adPo).map(a->{
                if(a!=null) {
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono deleteAd(Long adId){
        return advertiseRepository.findById(adId).flatMap(adPo->{
            return advertiseRepository.deleteAdvertisePoById(adId).map(it->{
                if(it!=0){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono uploadImg(Long adId, MultipartFile file){
        return advertiseRepository.findById(adId).flatMap(advertisePo -> {
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
                    advertisePo.setImagePath(url);
                    return advertiseRepository.save(advertisePo).map(a->{
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


    public Mono modifiedTime(Long adId, Long tid){
        return advertiseRepository.findById(adId).flatMap(advertisePo -> {
            return timeSegmentRepository.findById(tid).flatMap(timeSegmentPo -> {
                if(timeSegmentPo.getType()!=0){
                    log.info("该时段不是广告时段");
                    return Mono.just(new ReturnObject<>("该时段不是广告时段"));
                }
                return advertiseRepository.countAllBySegId(tid).flatMap(it->{
                    if(it<MAX_AD_LIMIT){
                        advertisePo.setSegId(tid);
                        return advertiseRepository.save(advertisePo).flatMap(a-> Mono.just(new ReturnObject<>(a)));
                    }
                    return Mono.just(new ReturnObject(ResponseCode.ADVERTISEMENT_OUTLIMIT));
                });
            }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"不存在该时段"));
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"不存在该广告"));
    }

    public Mono newAdvertise(Long tid, NewAdVo vo){
        return timeSegmentRepository.findById(tid).flatMap(timeSegmentPo -> {
            if(timeSegmentPo.getType()!=0){
                log.info("该时段不是广告时段");
                return Mono.just(new ReturnObject<>("该时段不是广告时段"));
            }
            return advertiseRepository.countAllBySegId(tid).flatMap(it->{
                if(it<MAX_AD_LIMIT){
                    AdvertisePo po=new AdvertisePo();
                    po.newPo(vo);
                    po.setSegId(tid);
                    return advertiseRepository.save(po).map(a->{
                        if(a!=null){
                            return new ReturnObject<>(a);
                        }
                        return null;
                    });
                }
                return Mono.just(new ReturnObject(ResponseCode.ADVERTISEMENT_OUTLIMIT));
            });
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,"不存在该时段"));
    }

    public Mono onShelves(Long id){
        return advertiseRepository.findById(id).flatMap(it->{
            it.setState(3);
            it.setGmtModified(LocalDateTime.now());
            return advertiseRepository.save(it).map(a->{
               if(a!=null){
                   return new ReturnObject<>();
               }
               return null;
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono offShelves(Long id){
        return advertiseRepository.findById(id).flatMap(it->{
            it.setState(4);
            it.setGmtModified(LocalDateTime.now());
            return advertiseRepository.save(it).map(a->{
                if(a!=null){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono auditAd(Long id){
        return advertiseRepository.findById(id).flatMap(it->{
            it.setState(2);
            it.setGmtModified(LocalDateTime.now());
            return advertiseRepository.save(it).map(a->{
                if(a!=null){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono getAdByTime(Long segId){
        return advertiseRepository.findAllBySegId(segId).collectList().map(it-> new ReturnObject(it));
    }
}
