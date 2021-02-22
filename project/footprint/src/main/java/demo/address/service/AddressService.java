package demo.address.service;

import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import demo.address.model.po.AddressPo;
import demo.address.model.po.RegionPo;
import demo.address.model.vo.NewAddressVo;
import demo.address.model.vo.NewRegionVo;
import demo.address.repository.AddressRepository;
import demo.address.repository.RegionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Slf4j
@Service
public class AddressService {

    @Resource
    AddressRepository addressRepository;

    @Resource
    RegionRepository regionRepository;

    private static Integer MAX_ADDRESS_COUNT=20;

    /**
     * TODO 分页后无法显示具体list
     * @param customerId
     * @param page
     * @param pageSize
     * @return
     */
    public Mono<ReturnObject> getAddress(Long customerId, Integer page, Integer pageSize){
        return addressRepository.findAllByCustomerId(customerId).collectList().map(it->{
            PageHelper.startPage(page, pageSize);
            PageInfo<AddressPo> addressPage=new PageInfo<>(it);
            return new ReturnObject(addressPage);
        });
    }

    public Mono<ReturnObject> newAddress(Long customerId, NewAddressVo newAddressVo){
        return addressRepository.countAllByCustomerId(customerId).flatMap(count->{
            //每个买家只能有20个地址
           if(count<MAX_ADDRESS_COUNT){
               AddressPo addressPo=new AddressPo();
               addressPo.setCustomerId(customerId);
               addressPo.trans(newAddressVo);
               return addressRepository.save(addressPo).map(po->{
                   if(po!=null){
                        return new ReturnObject<>(po);
                   }
                   return new ReturnObject<>("insert error");
               });
           }else {
               return Mono.just(new ReturnObject<>(ResponseCode.ADDRESS_OUTLIMIT));
           }
        });
    }

    public Mono setAddressDefault(Long customerId,Long addressId){
        return addressRepository.findById(addressId).flatMap(po->{
            return addressRepository.countAllByCustomerIdAndIsDefault(customerId,true).flatMap(count->{
                if(count>0){
                    return addressRepository.findByCustomerIdAndIsDefault(customerId,true).map(oldPo->{
                        if(oldPo.getId().equals(addressId)){
                            log.info("该地址已是默认地址");
                            return new ReturnObject<>();
                        }
                       oldPo.setIsDefault(false);
                        log.info("已存在，取消原有默认地址");
                       return addressRepository.save(oldPo).map(its-> setDefault(po));
                    });
                }
                return setDefault(po);
            });
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    private Mono<ReturnObject<Object>> setDefault(AddressPo po) {
        po.setIsDefault(true);
        return addressRepository.save(po).map(it->{
            if(it!=null){
                return new ReturnObject<>();
            }
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        });
    }

    public Mono modifiedAddress(Long customerId, Long addressId, NewAddressVo addressVo){
        return addressRepository.findById(addressId).flatMap(po->{
            if(!po.getCustomerId().equals(customerId)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }else{
                po.update(addressVo);
                return addressRepository.save(po).map(it->{
                    if(it!=null){
                        return new ReturnObject<>();
                    }
                    return null;
                });
            }
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono deleteAddress(Long customerId,Long addressId){
        return addressRepository.findById(addressId).flatMap(po-> {
            if (!po.getCustomerId().equals(customerId)) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }else{
                return addressRepository.deleteAddressPoById(addressId).map(it->{
                   if(it==1){
                       return new ReturnObject<>();
                   }
                   return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
                });
            }
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono newRegion(Long did, Long pid, NewRegionVo vo){
        return regionRepository.findById(pid).flatMap(po->{
            RegionPo newPo=new RegionPo();
            newPo.setPid(pid);
            newPo.setName(vo.getName());
            newPo.setPostalCode(Long.valueOf(vo.getPostalCode()));
            newPo.setGmtCreate(LocalDateTime.now());
            newPo.setGmtModified(LocalDateTime.now());
            return regionRepository.save(newPo).map(it->{
                if(it!=null){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject(ResponseCode.REGION_OBSOLETE));
    }

    public Mono modifiedRegion(Long did, Long id, NewRegionVo vo){
        return regionRepository.findById(id).flatMap(po->{
            po.setGmtModified(LocalDateTime.now());
            po.setName(vo.getName());
            po.setPostalCode(Long.valueOf(vo.getPostalCode()));
            return regionRepository.save(po).map(it->{
                if(it!=null){
                    return new ReturnObject<>();
                }
                return null;
            });
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono deleteRegion(Long did,Long id){
        return regionRepository.findById(id).flatMap(po->{
            return regionRepository.deleteRegionPoById(id).map(it->{
                if(it==1){
                    return new ReturnObject<>();
                }
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            });
        }).defaultIfEmpty(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
    }
}
