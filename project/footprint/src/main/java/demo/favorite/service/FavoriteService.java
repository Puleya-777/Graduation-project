package demo.favorite.service;

import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import demo.advertise.model.po.AdvertisePo;
import demo.favorite.model.po.FavoritePo;
import demo.favorite.model.vo.FavoriteVo;
import demo.favorite.repository.FavoriteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Service
@Slf4j
public class FavoriteService {

    @Resource
    FavoriteRepository favoriteRepository;

    public Mono getFavorite(Long customerId,Integer pageNum, Integer pageSize){
        return favoriteRepository.findAllByCustomerId(customerId).map(po->{
            /**
             * TODO 此处需要查SkuVo并填入
             */
            FavoriteVo vo=new FavoriteVo(po);
            return vo;
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

    public Mono newFavorite(Long customerId,Long skuId){
        return Mono.just(skuId).flatMap(aa->{
            FavoritePo po=new FavoritePo();
            po.setCustomerId(customerId);
            po.setGoodsSkuId(skuId);
            po.setGmtCreate(LocalDateTime.now());
            po.setGmtModified(LocalDateTime.now());
            return favoriteRepository.save(po).map(newPo->{
                if(newPo!=null){
                    /**
                     * TODO 此处需要查SkuVo并填入
                     */
                    FavoriteVo vo=new FavoriteVo(newPo);
                    return new ReturnObject(vo);
                }
                return null;
            });
        });
    }

    public Mono deleteFavorite(Long customerId,Long id){
        return favoriteRepository.findById(id).flatMap(po->{
            if(!po.getCustomerId().equals(customerId)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
            return favoriteRepository.deleteFavoritePoById(id).map(it->{
                if(it!=0){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }
}
