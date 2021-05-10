package com.order.controller;

import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.order.model.bo.FreightModel;
import com.order.model.bo.PieceFreightModel;
import com.order.model.bo.WeightFreightModel;
import com.order.model.vo.*;
import com.order.service.FreightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class FreightController {

    @Autowired
    private FreightService freightService;

    @Resource
    private HttpServletResponse httpServletResponse;

//    @DubboReference
//    private IAddressService addressServiceI;

    private  static  final Logger logger = LoggerFactory.getLogger(FreightController.class);

    /**
     * 计算运费
     */
    @PostMapping("/region/{rid}/price")
    public Mono calculateFreightPrice(@RequestBody List<OrderItemVo> vo, @PathVariable Long rid){
        logger.info("calculate freight service by regionId:" + rid);
//        int listSize = vo.size();
//        List<Integer> count = new ArrayList<>();
//        List<Long> skuId = new ArrayList<>();
//        System.out.println(rid);
//        for(int i=0;i<listSize;i++)
//        {
//            count.add(vo.get(i).getConut());
//            skuId.add(vo.get(i).getSkuId());
//        }
        return Mono.just(7);
//        return freightService.calcuFreightPrice(count, skuId, rid).map(Common::decorateReturnObject);

    }


    /**
     * 定义店铺的运费模板
     */
    @PostMapping("/shops/{id}/freightmodels")
    public Mono insertFreightModel(@RequestBody FreightModelVo vo, @PathVariable Long id) {
        logger.debug("insert freightmodel by shopId:" + id);
        FreightModel freightModel = vo.createFreightModel();
        freightModel.setShopId(id);
        freightModel.setDefaultModel((byte)0);
        return freightService.insertFreightModel(freightModel).map(retObject->{
            if(retObject.getCode()==ResponseCode.OK){
//                httpServletResponse.setStatus(HttpStatus.CREATED.value());
            }
            return retObject;
        }).map(Common::decorateReturnObject);
    }


    /**
     * 分页查询店铺的所有运费模板
     */
    @GetMapping("/shops/{id}/freightmodels")
    public Mono getShopAllFreightModels(@PathVariable("id") Long shopId,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        return freightService.getShopAllFreightModels(shopId,name,page,pageSize).map(Common::getPageRetObject);
    }


    /**
     * 管理员克隆店铺的运费模板
     */
    @PostMapping("/shops/{shopId}/freightmodels/{id}/clone")
    public Mono cloneShopFreightModel(@PathVariable("shopId") Long shopId,
                                        @PathVariable("id") long id){
        logger.debug("cloneShopFreightModel: shopId="+shopId+" id="+id);
        return freightService.cloneShopFreightModel(shopId,id).map(retObject->{
            if (retObject.getCode() == ResponseCode.OK){
//                httpServletResponse.setStatus(HttpStatus.CREATED.value());
                return Common.getRetObject(retObject);
            }else if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
            }
            else {
                return Common.decorateReturnObject(retObject);
            }
        });
    }


    /**
     * 通过id获得运费模板的概要
     */
    @GetMapping("/shops/{shopId}/freightmodels/{id}")
    public Mono getFreightModelById(@PathVariable("id") Long id,
                                      @PathVariable("shopId") Long shopId){
        return freightService.getFreightModelById(id).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                if(returnObject.getData().getShopId()!=shopId){
//                    httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                    return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
                }
                ReturnObject retObject=new ReturnObject(new FreightModelReturnVo(returnObject.getData()));
                return Common.getRetObject(retObject);
            } else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }


    /**
     * 修改运费模板
     */
    @PutMapping("/shops/{shopId}/freightmodels/{id}")
    public Mono changeFreightModel(@PathVariable("shopId") Long shopId,
                                     @PathVariable("id") Long id,
                                     @RequestBody FreightModelChangeVo freightModelChangeVo) {
        return freightService.changeFreightModel(id, freightModelChangeVo, shopId).map(ret->{
            if(ret.getCode().equals(ResponseCode.OK)){
                return Common.decorateReturnObject(ret);
            }else if(ret.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
            }
            return Common.decorateReturnObject(ret);
        });
    }


    /**
     * 删除运费模板，需同步删除与商品的
     */
    @DeleteMapping("/shops/{shopId}/freightmodels/{id}")
    public Mono delShopFreightModel(@PathVariable("shopId") Long shopId,
                                    @PathVariable("id") Long id) {
        logger.debug("delShopFreightModelById: id = "+id);
        return freightService.delShopFreightModel(shopId, id).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.decorateReturnObject(returnObject);
            }else if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);

            }else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }


    /**
     * 店家或管理员为商铺定义默认运费模板。
     */
    @PostMapping("/shops/{shopId}/freightmodels/{id}/default")
    public Mono postDefaultPieceFreight(@PathVariable("shopId") Long shopId,
                                          @PathVariable("id") Long id){
        return freightService.createDefaultPieceFreight(id,shopId).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.decorateReturnObject(returnObject);
            }else if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
            }else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }


    /***
     * 管理员定义管理员定义重量模板明细
     */
    @PostMapping("/shops/{shopId}/freightmodels/{id}/weightItems")
    public Mono postWeightFreightModel(@PathVariable Long shopId,
                                         @PathVariable Long id,
                                          @RequestBody WeightFreightModelVo vo){
        logger.debug("insert WeightFreightModel by shopId:" + shopId+"and id"+id);

        WeightFreightModel weightFreightModel = new WeightFreightModel(vo);

        weightFreightModel.setFreightModelId(id);
        weightFreightModel.setGmtCreate(LocalDateTime.now());
        weightFreightModel.setGmtModified(LocalDateTime.now());

//        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return freightService.insertWeightFreightModel(weightFreightModel).map(Common::decorateReturnObject);
    }


    /**
     * 查询某个重量运费模板明细
     */
    @GetMapping("/shops/{shopId}/freightmodels/{id}/weightItems")
    public Mono findWeightItemByFreightModelId(@PathVariable("shopId") Long shopId,
                                                 @PathVariable("id") Long id){
            return freightService.getWeightItemsByFreightModelId(shopId,id).map(returnObject->{
                if(returnObject.getCode() == ResponseCode.OK) {
                    return Common.getListRetObject(returnObject);
                } else {
                    return Common.decorateReturnObject(returnObject);
                }
            });
    }


    /***
     * 管理员定义件数模板明细
     */
    @PostMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Mono postPieceFreightModel(@PathVariable Long shopId,
                                        @PathVariable Long id,
                                        @RequestBody PieceFreightModelVo vo){
        logger.debug("update role by shopId:" + shopId+"and id"+id);

        PieceFreightModel pieceFreightModel = new PieceFreightModel(vo);

        pieceFreightModel.setFreightModelId(id);
        pieceFreightModel.setGmtCreate(LocalDateTime.now());
        pieceFreightModel.setGmtModified(LocalDateTime.now());
//        pieceFreightModel.setId((long) 1);
//        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return freightService.insertPieceFreightModel(pieceFreightModel).map(Common::decorateReturnObject);
    }


    /**
     * 查询某个件数运费模板明细
     */
    @GetMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object findPieceItemByFreightModelId(@PathVariable("shopId") Long shopId,
                                                @PathVariable("id") Long id) {
        return freightService.getPieceItemsByFreightModelId(shopId, id).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.getListRetObject(returnObject);
            } else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }



    /**
     * 修改重量运费模板明细
     */
    @PutMapping("/shops/{shopId}/weightItems/{id}")
    public Mono changeWeightFreightModel(@PathVariable("shopId") Long shopId,
                                           @PathVariable("id") Long id,
                                           @RequestBody WeightFreightModelChangeVo weightFreightModelChangeVo) {
        System.out.println("controller"+weightFreightModelChangeVo.getFirstWeightFreight());
        return freightService.changeWeightFreightModel(id, weightFreightModelChangeVo, shopId).map(ret->{
            if(ret.getCode().equals(ResponseCode.OK)){
                return Common.decorateReturnObject(ret);
            }else if(ret.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
            }
            return Common.decorateReturnObject(ret);
        });
    }


    /**
     * 删除重量运费模板明细
     */
    @DeleteMapping("/shops/{shopId}/weightItems/{id}")
    public Mono delWeightItemById(@PathVariable("shopId") Long shopId,
                                    @PathVariable("id") Long id){
        return freightService.delWeightItemById(shopId, id).map(returnObject->{
            if(returnObject.getCode().equals(ResponseCode.OK)){
                return Common.getRetObject(returnObject);
            }else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
            }else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
                return Common.decorateReturnObject(returnObject);
            }
            return Common.decorateReturnObject(returnObject);
        });
    }


    /**
     * 修改件数运费模板
     */
    @PutMapping("/shops/{shopId}/pieceItems/{id}")
    public Mono changePieceFreightModel(@PathVariable("shopId") Long shopId,
                                          @PathVariable("id") Long id,
                                          @RequestBody PieceFreightModelChangeVo pieceFreightModelChangeVo) {
        System.out.println("controller:"+pieceFreightModelChangeVo.getFirstItems());
        return freightService.changePieceFreightModel(id, pieceFreightModelChangeVo, shopId).map(ret->{
            if(ret.getCode().equals(ResponseCode.OK)){
                return Common.decorateReturnObject(ret);
            }else if(ret.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
            }
            return Common.decorateReturnObject(ret);
        });
    }


    /**
     * 删除件数运费模板明细
     */
    @DeleteMapping("/shops/{shopId}/pieceItems/{id}")
    public Mono delPieceItemById(@PathVariable("shopId") Long shopId,
                                   @PathVariable("id") Long id) {
        return freightService.delPieceItemById(shopId, id).map(returnObject->{
            if(returnObject.getCode().equals(ResponseCode.OK)){
                return Common.getRetObject(returnObject);
            }else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE) , httpServletResponse);
            }else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
                return Common.decorateReturnObject(returnObject);
            }
            return Common.getRetObject(returnObject);
        });
    }

    public Mono<List<Integer>> performanceTestWebflux(List<Integer> list){
        return Mono.just(list).map(x->{
            for (Integer i:x)
            {
                i+=2;
            }
            return x;
        });

    }

    public List<Integer> performanceTestNormal(List<Integer> list){
        for (Integer i:list)
        {
            i+=2;
        }
        return list;
    }
}
