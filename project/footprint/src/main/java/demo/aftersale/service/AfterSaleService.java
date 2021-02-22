package demo.aftersale.service;

import com.example.util.ReturnObject;
import demo.aftersale.model.po.AfterSalePo;
import demo.aftersale.model.vo.NewAfterSaleVo;
import demo.aftersale.repository.AfterSaleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author chei1
 */
@Service
@Slf4j
public class AfterSaleService {
    @Resource
    AfterSaleRepository afterSaleRepository;

    public Mono newAfterSale(Long orderItemId, Long customerId, NewAfterSaleVo vo){
        return Mono.just(vo).flatMap(it->{
            AfterSalePo afterSalePo=new AfterSalePo();
            afterSalePo.newPo(orderItemId,customerId,vo);
            return afterSaleRepository.save(afterSalePo).map(a->{
                if(a!=null){
                    return new ReturnObject<>(a);
                }
                return null;
            });
        });
    }
}
