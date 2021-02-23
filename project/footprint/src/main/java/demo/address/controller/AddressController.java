package demo.address.controller;

import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import demo.address.model.vo.NewAddressVo;
import demo.address.model.vo.NewRegionVo;
import demo.address.repository.RegionRepository;
import demo.address.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author chei1
 */
@RestController
@Slf4j
public class AddressController {

    @Resource
    RegionRepository regionRepository;

    @Autowired
    AddressService addressService;

    /**
     * 查询某个地区的所有上级地区
     */
    @GetMapping("region/{id}/ancestor")
    //@Audit
    public Mono<ReturnObject> getAncestor(@PathVariable Long id){
        return regionRepository.findAllByPid(id).collectList().map(it-> {
            log.info("find by pid:"+id);
          if(it.size()!=0){
              return new ReturnObject<>(it);
          }
          return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        });
    }

    /**
     * TODO 解析token获取customId；
     * 买家查询所有已有的地址信息
     */
    @GetMapping("addresses")
    //@Audit
    public Mono getAddress(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize){
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;
        return addressService.getAddress(101L,page,pageSize).map(Common::getPageRetObject);
    }

    /**
     * TODO 解析token获取customId；
     * 买家新增地址
     */
    @PostMapping("addresses")
    //@Audit
    public Mono newAddress(@RequestBody NewAddressVo newAddressPo){
        return addressService.newAddress(101L,newAddressPo);
    }

    /**
     * TODO 解析token获取customId；
     * 买家设置默认地址
     */
    @PutMapping("addresses/{id}/default")
    //@Audit
    public Mono defaultAddress(@PathVariable Long id){
        return addressService.setAddressDefault(101L,id);
    }

    /**
     * TODO 解析token获取customId；
     * 买家修改自己的地址信息
     */
    @PutMapping("addresses/{id}")
    //@Audit
    public Mono modifiedAddress(@PathVariable Long id,@RequestBody NewAddressVo newAddressVo){
        return addressService.modifiedAddress(101L,id,newAddressVo);
    }

    /**
     * TODO 解析token获取customId；
     * 买家删除地址
     */
    @DeleteMapping("addresses/{id}")
    public Mono deleteAddress(@PathVariable Long id){
        return addressService.deleteAddress(101L,id);
    }

    /**
     * 管理员在地区下新增子地区
     */
    @PostMapping("shops/{did}/regions/{id}/subregions")
    public Mono newRegion(@PathVariable Long did, @PathVariable Long id, @RequestBody NewRegionVo vo){
        return addressService.newRegion(did,id,vo);
    }

    /**
     * 管理员修改某个地区
     */
    @PutMapping("shops/{did}/regions/{id}")
    public Mono modifiedRegion(@PathVariable Long did, @PathVariable Long id, @RequestBody NewRegionVo vo){
        return addressService.modifiedRegion(did, id, vo);
    }

    /**
     * 管理员让某个地区无效
     */
    @DeleteMapping("shops/{did}/regions/{id}")
    public Mono deleteRegion(@PathVariable Long did, @PathVariable Long id){
        return addressService.deleteRegion(did, id);
    }



}
