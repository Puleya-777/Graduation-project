package demo.footprint.service;

import com.example.util.ReturnObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import demo.advertise.model.po.AdvertisePo;
import demo.footprint.model.vo.FootprintVo;
import demo.footprint.model.vo.SkuVo;
import demo.footprint.repository.FootprintRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author chei1
 */
@Service
@Slf4j
public class FootprintService {
    @Resource
    FootprintRepository footprintRepository;
    public Mono getAllFootprint(Integer pageNum, Integer pageSize){
        return footprintRepository.findAll().map(po->{
            /**
             * TODO 此处需要查SkuVo并填入
             */
            FootprintVo footprintVo=new FootprintVo(po);
            return footprintVo;
        }).collectList().map(it->{
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


    public Mono getFootprint(Long userId,Integer pageNum, Integer pageSize){
        return footprintRepository.findAllByCustomerId(userId).map(po->{
            /**
             * TODO 此处需要查SkuVo并填入
             */
            FootprintVo footprintVo=new FootprintVo(po);
            return footprintVo;
        }).collectList().map(it->{
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
