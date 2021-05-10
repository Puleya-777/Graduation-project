package com.order.service;

import com.example.model.VoObject;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import com.order.dao.FreightDao;
import com.order.model.bo.*;
import com.order.model.po.*;
import com.order.model.vo.*;
import com.order.feign.GoodsFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

//import com.goodsClient.model.GoodsFreightDTO;

@Service
public class FreightService {
    @Autowired
    private FreightDao freightDao;

    @Autowired
    private GoodsFeign goodsFeign;


    private Logger logger = LoggerFactory.getLogger(FreightService.class);

    /**
     * 新增店铺的运费模板
     */
    @Transactional
    public Mono<ReturnObject<VoObject>> insertFreightModel(FreightModel freightModel) {
        return freightDao.insertFreightModel(freightModel).map(retObj->{
            if(retObj.getCode().equals(ResponseCode.OK)){
                return new ReturnObject<>((VoObject) retObj.getData());
            }else{
                return new ReturnObject( retObj.getCode(), retObj.getErrmsg());
            }
        });
    }

    /**
     * 分页查询店铺的所有运费模板
     */
    public Mono<ReturnObject<PageInfo<VoObject>>> getShopAllFreightModels(Long shopId, String name, Integer page, Integer pageSize) {
        return freightDao.getShopAllFreightModels(shopId,name,page,pageSize);
    }

    /**
     * 管理员克隆店铺的运费模板。
     */
    public Mono<ReturnObject> cloneShopFreightModel(Long shopId, long id) {
        return freightDao.insertCloneFreightModel(shopId,id).flatMap(cloneFreightModelRetObj->{
            if(cloneFreightModelRetObj.getCode().equals(ResponseCode.OK)){
                FreightModelReturnVo cloneFreightModel=cloneFreightModelRetObj.getData();
                short type=cloneFreightModel.getType();
                long newId=cloneFreightModel.getId();
                return freightDao.insertCloneFreightModelInfo(id,newId,type).map(res->cloneFreightModelRetObj);
            }else {
                return Mono.just(cloneFreightModelRetObj);
            }
        });

    }

    /**
     * 通过id获得运费模板的概要
     */
    public Mono<ReturnObject<FreightModelPo>> getFreightModelById(Long id) {
        return freightDao.getFreightModelById(id);
    }

    /**
     * 修改运费模板
     */
    public Mono<ReturnObject<ResponseCode>> changeFreightModel(Long id, FreightModelChangeVo freightModelChangeVo, Long shopId) {
        FreightModelChangeBo freightModelChangeBo = freightModelChangeVo.createFreightModelBo();
        freightModelChangeBo.setShopId(shopId);
        freightModelChangeBo.setId(id);

        return freightDao.changeFreightModel(freightModelChangeBo);
    }

    /**
     * 删除运费模板，需同步删除与商品的
     */
    public Mono<ReturnObject> delShopFreightModel(Long shopId, Long id){
        //物理删除
        return freightDao.delShopFreightModel(shopId,id);
        //TODO:调用商品模块的API改商品的freight_id
    }


    /**
     * 店家或管理员为商铺定义默认运费模板。
     */
    public Mono<ReturnObject<VoObject>> createDefaultPieceFreight(Long id,Long shopId){
        return freightDao.putDefaultPieceFreight(id,shopId).map(retObj->{
            if (retObj.getCode().equals(ResponseCode.OK)) {
                return new ReturnObject<>(retObj.getData());
            } else {
                return new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
            }
        });
    }


    /**
     * 管理员定义管理员定义重量模板明细
     */
    @Transactional
    public Mono<ReturnObject<VoObject>> insertWeightFreightModel(WeightFreightModel weightFreightModel) {
        return freightDao.insertWeightFreightModel(weightFreightModel).map(retObj->{
            ReturnObject<VoObject> retWeightFreightModel;
            if (retObj.getCode().equals(ResponseCode.OK)) {
                retWeightFreightModel = new ReturnObject<>(retObj.getData());
            } else {
                retWeightFreightModel = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
            }
            return retWeightFreightModel;
        });
    }

    /**
     * 查询某个重量运费模板明细
     */
    @Transactional
    public Mono<ReturnObject<List>> getWeightItemsByFreightModelId(Long shopId, Long id)
    {
        return freightDao.getWeightItemByFreightModelId(shopId, id);
    }

    /**
     * 管理员定义件数模板明细
     */
    @Transactional
    public Mono<ReturnObject<VoObject>> insertPieceFreightModel(PieceFreightModel pieceFreightModel) {
        return freightDao.insertPieceFreightModel(pieceFreightModel).map(retObj->{
            ReturnObject<VoObject> retPieceFreightModel;
            if (retObj.getCode().equals(ResponseCode.OK)) {
                retPieceFreightModel = new ReturnObject<>(retObj.getData());
            } else {
                retPieceFreightModel = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
            }
            return retPieceFreightModel;
        });
    }

    /**
     * 查询某个件数运费模板明细
     */
    @Transactional
    public Mono<ReturnObject<List>> getPieceItemsByFreightModelId(Long shopId, Long id)
    {
        return freightDao.getPieceItemByFreightModelId(shopId,id);
    }

    /**
     * 修改重量运费模板明细
     */
    public Mono<ReturnObject<ResponseCode>> changeWeightFreightModel(Long id, WeightFreightModelChangeVo weightFreightModelChangeVo,
                                                               Long shopId)
    {
        WeightFreightModelChangeBo weightFreightModelChangeBo = weightFreightModelChangeVo.createWeightFreightModelBo();
        weightFreightModelChangeBo.setId(id);
        System.out.println("service"+weightFreightModelChangeBo.getFirstWeightFreight());
        return freightDao.changeWeightFreightModel(weightFreightModelChangeBo, shopId);
    }

    /**
     * 删除某个重量运费模板明细
     */
    @Transactional
    public Mono<ReturnObject<VoObject>> delWeightItemById(Long shopId, Long id)
    {
        return freightDao.delWeightItemById(shopId, id);
    }

    /**
     * 修改件数运费模板
     */
    public Mono<ReturnObject<ResponseCode>> changePieceFreightModel(Long id, PieceFreightModelChangeVo pieceFreightModelChangeVo,
                                                              Long shopId)
    {
        PieceFreightModelChangeBo pieceFreightModelChangeBo = pieceFreightModelChangeVo.createPieceFreightModelChangeBo();
        pieceFreightModelChangeBo.setId(id);
        System.out.println("service:"+pieceFreightModelChangeBo.getFirstItems());

        return  freightDao.changePieceFreightModel(pieceFreightModelChangeBo, shopId);
    }

    /**
     * 删除某个件数运费模板明细
     */
    @Transactional
    public Mono<ReturnObject> delPieceItemById(Long shopId, Long id)
    {
        return freightDao.delPieceItemById(shopId, id);
    }

    /**
     * 计算运费
     */
    public Mono<ReturnObject<Long>> calcuFreightPrice(List<Integer> count, List<Long> skuId,Long regionId) {
        return Mono.just((new ReturnObject<>((long)17)));

    }


}
