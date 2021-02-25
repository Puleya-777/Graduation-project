package demo.aftersale.service;

import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import demo.address.model.vo.ReturnAddressVo;
import demo.advertise.model.po.AdvertisePo;
import demo.advertise.model.vo.ModifiedAdVo;
import demo.aftersale.model.po.AfterSalePo;
import demo.aftersale.model.vo.*;
import demo.aftersale.repository.AfterSaleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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

    public Mono getAllAfterSale(Long customerId,Integer pageNum,Integer pageSize,Integer state,Integer type){
        return afterSaleRepository.findAllByCustomerIdAndBeDeleted(customerId,false).collectList().map(poList->{
           if(state!=null){
               poList=poList.stream().filter(afterSalePo -> state.equals(afterSalePo.getState())).collect(Collectors.toList());
           }
           if (type!=null){
               poList=poList.stream().filter(afterSalePo -> type.equals(afterSalePo.getType())).collect(Collectors.toList());
           }
           return poList;
        }).map(it->{
            Page page = new Page(pageNum, pageSize);
            int total = it.size();
            page.setTotal(total);
            int startIndex = Math.min((pageNum - 1) * pageSize,total);
            int endIndex = Math.min(startIndex + pageSize,total);
            log.info("s:"+startIndex+"  e:"+endIndex);
            page.addAll(it.subList(startIndex,endIndex));
            PageInfo<ReturnAddressVo> retPage=new PageInfo(page);
            return new ReturnObject(retPage);
        });
    }

    public Mono getAllAfterSaleAdmin(Integer pageNum,Integer pageSize,Integer state,Integer type){
        return afterSaleRepository.findAllByBeDeleted(false).collectList().map(poList->{
            if(state!=null){
                poList=poList.stream().filter(afterSalePo -> state.equals(afterSalePo.getState())).collect(Collectors.toList());
            }
            if (type!=null){
                poList=poList.stream().filter(afterSalePo -> type.equals(afterSalePo.getType())).collect(Collectors.toList());
            }
            return poList;
        }).map(it->{
            Page page = new Page(pageNum, pageSize);
            int total = it.size();
            page.setTotal(total);
            int startIndex = Math.min((pageNum - 1) * pageSize,total);
            int endIndex = Math.min(startIndex + pageSize,total);
            log.info("s:"+startIndex+"  e:"+endIndex);
            page.addAll(it.subList(startIndex,endIndex));
            PageInfo<ReturnAddressVo> retPage=new PageInfo(page);
            return new ReturnObject(retPage);
        });
    }

    public Mono getById(Long customerId,Long id){
        return afterSaleRepository.findByIdAndBeDeleted(id,false).map(po->{
            if(po.getCustomerId().equals(customerId)){
                return new ReturnObject<>(po);
            }
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono ModifiedById(Long customerId, Long id, ModifiedAfterSaleVo vo){
        return afterSaleRepository.findById(id).flatMap(po->{
            if(!po.getCustomerId().equals(customerId)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
            if(po.getState()!=1){
                return Mono.just(new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW));
            }
            po.modifiedPo(vo);
            return afterSaleRepository.save(po).map(it->{
                if(it!=null){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono deleteById(Long customerId,Long id){
        return afterSaleRepository.findByIdAndBeDeleted(id,false).flatMap(po->{
            if(!po.getCustomerId().equals(customerId)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
            if(po.getState()==7){
                po.setBeDeleted(true);
            }else {
                po.setState(8);
            }
            return afterSaleRepository.save(po).map(it->{
                if(it!=null){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }
    public Mono sendBack(Long customerId,Long id,String logSn){
        return afterSaleRepository.findByIdAndBeDeleted(id,false).flatMap(po->{
            if(!po.getCustomerId().equals(customerId)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
            if(po.getState()==2){
                po.setCustomerLogSn(logSn);
                po.setState(3);
                return afterSaleRepository.save(po).map(it->{
                    if(it!=null){
                        return new ReturnObject<>();
                    }
                    return null;
                });

            }
            return Mono.just(new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW));
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono confirm(Long customerId,Long id){
        return afterSaleRepository.findByIdAndBeDeleted(id,false).flatMap(po->{
            if(!po.getCustomerId().equals(customerId)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
            po.setState(7);
            return afterSaleRepository.save(po).map(it->{
                if(it!=null){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono getByIdAdmin(Long id){
        return afterSaleRepository.findById(id).map(it->{
            if(it!=null){
                return new ReturnObject<>(it);
            }
            return null;
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono adminConfirm(Long id,AdminConfirmVo vo){
        return afterSaleRepository.findByIdAndBeDeleted(id,false).flatMap(po->{
            if(po.getState()==1){
                po.adminConfirm(vo);
                return afterSaleRepository.save(po).map(it->{
                    if(it!=null){
                        return new ReturnObject<>();
                    }
                    return null;
                });
            }
            return Mono.just(new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW));
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono adminReceive (Long id, AdminReceiveVo vo){
        return afterSaleRepository.findByIdAndBeDeleted(id,false).flatMap(po->{
            if(po.getState()==3){
                po.adminReceive(vo);
                return afterSaleRepository.save(po).map(it->{
                    if(it!=null){
                        return new ReturnObject<>();
                    }
                    return null;
                });
            }
            return Mono.just(new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW));
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono adminDeliver(Long id,AdminDeliverVo vo){
        return afterSaleRepository.findByIdAndBeDeleted(id,false).flatMap(po->{
            if(po.getState()==4){
                po.adminDeliver(vo);
                return afterSaleRepository.save(po).map(it->{
                    if(it!=null){
                        return new ReturnObject<>();
                    }
                    return null;
                });
            }
            return Mono.just(new ReturnObject<>(ResponseCode.AFTERSALE_STATENOTALLOW));
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }
}
