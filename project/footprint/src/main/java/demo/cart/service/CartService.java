package demo.cart.service;

import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import demo.advertise.model.po.AdvertisePo;
import demo.cart.model.po.CartPo;
import demo.cart.model.vo.NewCartVo;
import demo.cart.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author chei1
 */
@Service
@Slf4j
public class CartService {
    @Resource
    CartRepository cartRepository;

    public Mono getCarts(Long customerId,Integer pageNum, Integer pageSize){
        return cartRepository.findAllByCustomerId(customerId).collectList().map(it->{
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

    public Mono newCarts(Long customerId, NewCartVo newCartVo){
        return cartRepository.countAllByCustomerIdAndGoodsSkuId(customerId,newCartVo.getGoodsSkuId()).flatMap(cout->{
            if(cout==0){
                CartPo cartPo=new CartPo();
                /*
                 * TODO 此处需要计算price
                 */
                cartPo.trans(customerId,newCartVo);
                return cartRepository.save(cartPo).map(it->{
                    if(it!=null){
                        return new ReturnObject<>(it);
                    }
                    return null;
                });
            }else {
                return cartRepository.findAllByCustomerIdAndGoodsSkuId(customerId,newCartVo.getGoodsSkuId()).flatMap(po->{
                    po.setQuantity(po.getQuantity()+ newCartVo.getQuantity());
                    //并重新计算price
                    return cartRepository.save(po).map(it->{
                        if(it!=null){
                            return new ReturnObject<>(it);
                        }
                        return null;
                    });
                });
            }
        });
    }

    public Mono deleteAll(Long customerId){
        return cartRepository.deleteCartPosByCustomerId(customerId).map(it-> new ReturnObject());
    }

    public Mono deleteCart(Long customerId,Long id){
        return cartRepository.findById(id).flatMap(po->{
            if(!po.getCustomerId().equals(customerId)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }else{
                return cartRepository.deleteCartPoById(id).map(it->{
                    if(it!=0){
                        return new ReturnObject<>();
                    }
                    return null;
                });
            }
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono modifiedCart(Long customerId,Long id,NewCartVo vo){
        return cartRepository.findById(id).flatMap(po->{
            if(!po.getCustomerId().equals(customerId)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }else{
                po.trans(customerId,vo);
                return cartRepository.save(po).map(it->{
                    if(it!=null){
                        return new ReturnObject<>(it);
                    }else {
                        return null;
                    }
                });
            }
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }
}
