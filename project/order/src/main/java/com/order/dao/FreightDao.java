package com.order.dao;

import com.example.model.VoObject;
import com.example.util.RandomCaptcha;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.order.model.bo.*;
import com.order.model.po.*;
import com.order.model.vo.*;
import com.order.repository.FreightModelRepository;
import com.order.repository.PieceFreightModelRepository;
import com.order.repository.WeightFreightModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FreightDao {
    private static final Logger logger = LoggerFactory.getLogger(FreightDao.class);
    @Autowired
    private FreightModelRepository freightModelRepository;

    @Autowired
    private PieceFreightModelRepository pieceFreightModelRepository;

    @Autowired
    private WeightFreightModelRepository weightFreightModelRepository;

    /**
     * 增加一个店铺定义的运费模板
     */
    public Mono<ReturnObject<FreightModelReturnVo>> insertFreightModel(FreightModel freightModel) {
        FreightModelPo freightModelPo = freightModel.gotFreightModelPo();
        return freightModelRepository.save(freightModelPo).flatMap(ret->{
            if(ret == null)//插入失败
            {
                logger.debug("insertFreightModel: insert freightModel fail " + freightModelPo.toString());
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + freightModelPo.getName())));
            }
            else{//成功
                logger.debug("insertFreightModel: insert freightModel = " + freightModelPo.toString());
                return freightModelRepository.findById(freightModelPo.getId()).map(res->{
                    FreightModelReturnVo freightModel1=new FreightModelReturnVo(res);
                    return new ReturnObject<>(freightModel1);
                });
            }
        });
    }

    /**
     * 分页查询店铺的所有运费模板
     */
    public Mono<ReturnObject<PageInfo<VoObject>>> getShopAllFreightModels(Long shopId, String name, Integer page, Integer pageSize){
        PageHelper.startPage(page, pageSize);
        Mono<PageInfo<FreightModelPo>> FreightModelPos;
        if(name==null||name.isBlank()){
            System.out.println(1);
            FreightModelPos = freightModelRepository.findByShopId(shopId).collect(Collectors.toList()).map(PageInfo::new);
        }else {
            System.out.println(2);
            FreightModelPos = freightModelRepository.findByShopIdAndName(shopId,name).collect(Collectors.toList()).map(PageInfo::new);
        }
        Mono<List<VoObject>> FreightModels = FreightModelPos.map(pageInfo->pageInfo.getList().stream().map(FreightModel::new).collect(Collectors.toList()));
        return Mono.zip(FreightModelPos,FreightModels).map(tuple->{
            PageInfo<VoObject> returnObject = new PageInfo<>(tuple.getT2());
            returnObject.setPages(tuple.getT1().getPages());
            returnObject.setPageNum(tuple.getT1().getPageNum());
            returnObject.setPageSize(tuple.getT1().getPageSize());
            returnObject.setTotal(tuple.getT1().getTotal());
            return new ReturnObject<>(returnObject);
        });

    }

    /**
     * 插入店铺的运费模板概要。
     */
    public Mono<ReturnObject<FreightModelReturnVo>> insertCloneFreightModel(Long shopId, long id) {
        return freightModelRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional->{
            if (!resOptional.isPresent()){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }else {
                FreightModelPo cloneFreightModelPo = resOptional.get();
                if(!(cloneFreightModelPo.getShopId().equals(shopId))){
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
                }
                if(cloneFreightModelPo.getShopId().toString().equals("null")){
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
                }
                //id置为null
                cloneFreightModelPo.setId(null);
                //改变shopId
                cloneFreightModelPo.setShopId(shopId);
                //在模板name后面加随机数
                cloneFreightModelPo.setName(cloneFreightModelPo.getName()+ RandomCaptcha.getRandomString(6));

                cloneFreightModelPo.setDefaultModel((byte) 0);
                cloneFreightModelPo.setGmtCreate(LocalDateTime.now());
                cloneFreightModelPo.setGmtModified(LocalDateTime.now());
                return freightModelRepository.save(cloneFreightModelPo).map(res->{
                    logger.debug("insertCloneFreightModel: insert cloneFreightModel = " +cloneFreightModelPo.toString());
                    //po生成bo并返回
                    return new ReturnObject<>(new FreightModelReturnVo(res));
                });
            }
        });
    }

    /**
     * 插入店铺的运费模板详情。
     */
    public Mono<ReturnObject> insertCloneFreightModelInfo(long oldId, long newId , short type) {

        //先判断类型，如果是0为重量，1为件数
        if(type==0){
            return weightFreightModelRepository.findByFreightModelId(oldId).collect(Collectors.toList()).flatMap(cloneWeightFreightModelPoS->{
                if(cloneWeightFreightModelPoS.isEmpty()){
                    //插入失败
                    logger.error("getFreightModelById: 数据库不存在该重量运费模板 freightmodel_id=" + oldId);
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "新增重量运费模板失败：freightmodel_id=" + newId));
                }
                for(WeightFreightModelPo cloneWeightFreightModelPo:cloneWeightFreightModelPoS) {
                    //id置为null
                    cloneWeightFreightModelPo.setId(null);

                    //运费模板id置为新的
                    cloneWeightFreightModelPo.setFreightModelId(newId);

                    cloneWeightFreightModelPo.setGmtCreate(LocalDateTime.now());
                    cloneWeightFreightModelPo.setGmtModified(LocalDateTime.now());

                    return weightFreightModelRepository.save(cloneWeightFreightModelPo).map(res->{
                        //po生成bo并返回
                        return new ReturnObject<>(new WeightFreightModel(cloneWeightFreightModelPo));
                    });
                }
                return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s")));
            });

        }else {
            return pieceFreightModelRepository.findByFreightModelId(oldId).collect(Collectors.toList()).flatMap(clonePieceFreightModelPoS->{
                if(clonePieceFreightModelPoS.isEmpty()){
                    //插入失败
                    logger.error("getFreightModelById: 数据库不存在该件数运费模板 freightmodel_id=" + oldId);
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "新增件数运费模板失败：freightmodel_id=" + newId));
                }
                for(PieceFreightModelPo clonePieceFreightModelPo:clonePieceFreightModelPoS) {
                    //id置为null
                    clonePieceFreightModelPo.setId(null);

                    //运费模板id置为新的
                    clonePieceFreightModelPo.setFreightModelId(newId);

                    clonePieceFreightModelPo.setGmtCreate(LocalDateTime.now());
                    clonePieceFreightModelPo.setGmtModified(LocalDateTime.now());

                    return pieceFreightModelRepository.save(clonePieceFreightModelPo).map(res->{
                        //po生成bo并返回
                        return new ReturnObject<>(new PieceFreightModel(clonePieceFreightModelPo));
                    });
                }
                return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s")));
            });
        }
    }

    /**
     * 通过id获得运费模板的概要
     */
    public Mono<ReturnObject<FreightModelPo>> getFreightModelById(Long id){
        return freightModelRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional->{
            if (!resOptional.isPresent()){
                logger.error("getFreightModelById: 数据库不存在该运费模板 freightmodel_id=" + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }else {
                return Mono.just(new ReturnObject<>(resOptional.get()));
            }
        });
    }

    /**
     * 修改运费模板
     */
    public Mono<ReturnObject<ResponseCode>> changeFreightModel(FreightModelChangeBo freightModelChangeBo) {
        return  freightModelRepository.findById(freightModelChangeBo.getId()).flatMap(res->Mono.just(Optional.of(res)))
                .defaultIfEmpty(Optional.empty()).flatMap(resOptional->{
            if (!resOptional.isPresent()){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            FreightModelPo freightModelPo = resOptional.get();
            if(!(freightModelPo.getShopId().equals(freightModelChangeBo.getShopId()))||freightModelPo.getShopId().toString().equals("null")){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
            String name = freightModelChangeBo.getName();
            if (name != null) {
                return freightModelRepository.findByName(name).collect(Collectors.toList()).flatMap(freightModelPoList->{
                    if (freightModelPoList.size() > 0) {
                        logger.info(freightModelPoList.get(0).toString());
                        return Mono.just(new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME));
                    }
                    freightModelPo.setUnit(freightModelChangeBo.getUnit());
                    freightModelPo.setName(freightModelChangeBo.getName());
                    return freightModelRepository.save(freightModelPo).map(res->new ReturnObject<>());
                });
            }
            freightModelPo.setUnit(freightModelChangeBo.getUnit());

            return freightModelRepository.save(freightModelPo).map(res->new ReturnObject<>());

        });
    }

    /**
     * 删除运费模板，需同步删除与商品的
     */
    public Mono<ReturnObject> delShopFreightModel(Long shopId, Long id) {
        return freightModelRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional->{
            if (!resOptional.isPresent()){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            FreightModelPo po = resOptional.get();
            if(!(po.getShopId().equals(shopId))||po.getShopId().toString().equals("null")){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
            return Mono.zip(freightModelRepository.deleteFreightModelById(id),weightFreightModelRepository.deleteByFreightModelId(id),pieceFreightModelRepository.deleteByFreightModelId(id)).map(tuple->new ReturnObject<>(ResponseCode.OK));
        });
    }

    /**
     * 店家或管理员为商铺定义默认运费模板。
     */
    public Mono<ReturnObject<FreightModel>> putDefaultPieceFreight(Long id, Long shopid){
        return freightModelRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            return freightModelRepository.findByShopId(shopid).collect(Collectors.toList()).flatMap(res -> Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional2 -> {
                if (!resOptional2.isPresent()) {
                    return Mono.just(new ReturnObject<>(ResponseCode.SHOP_ID_NOTEXIST, String.format("不存在对应的shopid")));
                }
                List<FreightModelPo> Pos = resOptional2.get();
                for (FreightModelPo freightModelPo : Pos) {
                    if (freightModelPo.getDefaultModel() == 1 && freightModelPo.getId().equals(id)) {
                        //已存在对应的默认模板
                        logger.debug("updateFreightModel: update freightModel fail " + freightModelPo.toString());
                        return Mono.just(new ReturnObject<>(ResponseCode.DEFAULTMODEL_EXISTED, String.format("已经存在对应的默认模板，新增失败")));
                    } else {
                        if (freightModelPo.getId().equals(id)) {//将对应id的模板设置为默认模板
                            freightModelPo.setDefaultModel((byte) 1);
                            freightModelPo.setGmtModified(LocalDateTime.now());
                            return freightModelRepository.save(freightModelPo).flatMap(res -> {
                                for (FreightModelPo old : Pos) {
                                    //将原商店的默认模板恢复为普通模板
                                    if (old.getDefaultModel() == 1 && !(old.getId().equals(id))) {
                                        old.setDefaultModel((byte) 0);
                                        old.setGmtModified(LocalDateTime.now());
                                        return freightModelRepository.save(old).map(res2 -> new ReturnObject<>(ResponseCode.OK, String.format("成功")));
                                    }
                                }
                                return Mono.just(new ReturnObject<>(ResponseCode.OK, String.format("成功")));
                            });
                        }
                    }
                }
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("失败：")));
            });
        });
    }

    /**
     * 管理员定义重量模板明细
     */
    public  Mono<ReturnObject<WeightFreightModel>> insertWeightFreightModel(WeightFreightModel weightFreightModel) {
        WeightFreightModelPo weightFreightModelPo = weightFreightModel.gotWeightFreightModelPo();
        return weightFreightModelRepository.save(weightFreightModelPo).map(res->{
            logger.debug("insertPieceFreightModel: insert pieceFreightModel = " + weightFreightModelPo.toString());
            weightFreightModel.setId(weightFreightModelPo.getId());
            return new ReturnObject<>(weightFreightModel);
        });
    }

    /**
     * 查询某个重量运费模板明细
     */
    public Mono<ReturnObject<List>> getWeightItemByFreightModelId(Long shopId, Long id){
        return freightModelRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            FreightModelPo freightModelPo = resOptional.get();
            if(shopId != freightModelPo.getShopId())
            {
                return Mono.just(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("店铺id不匹配：" + shopId)));
            }
            return weightFreightModelRepository.findByFreightModelId(id).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional2-> {
                if (!resOptional2.isPresent()) {
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                }
                List<WeightFreightModelPo> weightFreightModelPos = resOptional2.get();
                List<WeightFreightModel> weightFreightModels = new ArrayList<>();
                for (WeightFreightModelPo weightFreightModelPo:weightFreightModelPos)
                {
                    WeightFreightModel weightFreightModel = new WeightFreightModel(weightFreightModelPo);
                    weightFreightModels.add(weightFreightModel);
                }
                return Mono.just(new ReturnObject<>(weightFreightModels));
            });
        });
    }

    /**
     * 管理员定义件数模板明细
     */
    public  Mono<ReturnObject<PieceFreightModel>> insertPieceFreightModel(PieceFreightModel pieceFreightModel) {
        PieceFreightModelPo pieceFreightModelPo = pieceFreightModel.gotPieceFreightModelPo();
        return pieceFreightModelRepository.save(pieceFreightModelPo).map(res->{
            logger.debug("insertPieceFreightModel: insert pieceFreightModel = " + pieceFreightModelPo.toString());
            pieceFreightModel.setId(res.getId());
            return new ReturnObject<>(pieceFreightModel);
        });
    }

    /**
     * 查询某个件数运费模板明细
     */
    public Mono<ReturnObject<List>> getPieceItemByFreightModelId(Long shopId, Long id) {
        return freightModelRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            FreightModelPo freightModelPo = resOptional.get();
            if(shopId != freightModelPo.getShopId())
            {
                return Mono.just(new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("店铺id不匹配：" + shopId)));
            }
            return pieceFreightModelRepository.findByFreightModelId(id).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional2-> {
                if (!resOptional2.isPresent()) {
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                }
                List<PieceFreightModelPo> pieceFreightModelPos = resOptional2.get();
                List<PieceFreightModel> pieceFreightModels = new ArrayList<>();
                for (PieceFreightModelPo pieceFreightModelPo:pieceFreightModelPos)
                {
                    PieceFreightModel pieceFreightModel = new PieceFreightModel(pieceFreightModelPo);
                    pieceFreightModels.add(pieceFreightModel);
                }
                return Mono.just(new ReturnObject<>(pieceFreightModels));
            });
        });
    }

    /**
     * 修改重量运费模板
     */
    public Mono<ReturnObject<ResponseCode>> changeWeightFreightModel(WeightFreightModelChangeBo weightFreightModelChangeBo,
                                                                     Long shopId) {
        return weightFreightModelRepository.findById(weightFreightModelChangeBo.getId()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            WeightFreightModelPo weightFreightModelPo = weightFreightModelChangeBo.gotWeightFreightModelPo();
            WeightFreightModelPo retWeightFreightModel = resOptional.get();
            Long freightModelId = retWeightFreightModel.getFreightModelId();
            weightFreightModelPo.updatePo(retWeightFreightModel);
            weightFreightModelPo.setGmtModified(LocalDateTime.now());
            System.out.println("dao"+weightFreightModelPo.getFirstWeightFreight());
            return freightModelRepository.findById(freightModelId).flatMap(freightModelPo->{
                // 判断商家与运费模板是否匹配
                if (freightModelPo.getShopId().equals(shopId)) {
                    return weightFreightModelRepository.save(weightFreightModelPo).map(res->new ReturnObject<>());
                }
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            });

        });
    }

    /**
     * 删除某个重量运费模板明细
     */
    public Mono<ReturnObject<VoObject>> delWeightItemById(Long shopId, Long id) {
        return weightFreightModelRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.info("模板明细不存在或已被删除：id = " + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            WeightFreightModelPo weightFreightModelPo = resOptional.get();
            return freightModelRepository.findById(weightFreightModelPo.getFreightModelId()).flatMap(freightModelPo->{
                if(freightModelPo.getShopId() != shopId)
                {
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("店铺id不匹配：" + shopId)));
                }
                return weightFreightModelRepository.deleteWeightFreightModelById(id).map(res->{
                    logger.info("模板明细 id = " + id + " 已被永久删除");
                    return new ReturnObject<>();
                });
            });
        });
    }

    /**
     * 修改件数运费模板
     */
    public Mono<ReturnObject<ResponseCode>> changePieceFreightModel(PieceFreightModelChangeBo pieceFreightModelChangeBo,
                                                              Long shopId) {
        return pieceFreightModelRepository.findById(pieceFreightModelChangeBo.getId()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            PieceFreightModelPo pieceFreightModelPo = pieceFreightModelChangeBo.gotPieceFreightModelPo();
            PieceFreightModelPo retPieceFreightModel = resOptional.get();
            Long freightModelId = retPieceFreightModel.getFreightModelId();
            pieceFreightModelPo.updatePo(retPieceFreightModel);
            pieceFreightModelPo.setGmtModified(LocalDateTime.now());
            System.out.println(pieceFreightModelPo.getFirstItems());
            return freightModelRepository.findById(freightModelId).flatMap(freightModelPo->{
                // 判断商家与运费模板是否匹配
                if (freightModelPo.getShopId().equals(shopId)) {
                    return pieceFreightModelRepository.save(pieceFreightModelPo).map(res->new ReturnObject<>());
                }
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            });
        });
    }


    /**
     * 删除某个件数运费模板明细
     */
    public Mono<ReturnObject> delPieceItemById(Long shopId, Long id) {
        return pieceFreightModelRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.info("模板明细不存在或已被删除：id = " + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            PieceFreightModelPo pieceFreightModelPo = resOptional.get();
            return freightModelRepository.findById(pieceFreightModelPo.getFreightModelId()).flatMap(freightModelPo->{
                if(freightModelPo.getShopId() != shopId)
                {
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("店铺id不匹配：" + shopId)));
                }
                return pieceFreightModelRepository.deletePieceFreightModelById(id).map(res->{
                    logger.info("模板明细 id = " + id + " 已被永久删除");
                    return new ReturnObject<>();
                });
            });
        });
    }

    /**
     * 通过shopId获得该店铺默认运费模板
     */
    public Mono<FreightModelPo> getDefaultFreightModelByshopId(Long shopId) {
        return freightModelRepository.findByShopIdAndDefaultModel(shopId,(byte)1).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error("getFreightModelById: 数据库不存在该默认运费模板 freightmodel_id=");
                return Mono.just(null);
            }
            return Mono.just(resOptional.get().get(0));
        });
    }

    /**
     * 查询某个件数运费模板明细 Piece
     */
    public Mono<ReturnObject<PieceFreightModel>> getPieceItemByFreightModelIdRegionId(Long shopId, Long id,Long RegionId) {
        return pieceFreightModelRepository.findByFreightModelIdAndRegionId(id,RegionId).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.FREIGHTMODEL_SHOP_NOTFIT));
            }
            List<PieceFreightModelPo> pieceFreightModelPos = resOptional.get();
            PieceFreightModel pieceFreightModel=new PieceFreightModel(pieceFreightModelPos.get(0));
            return Mono.just(new ReturnObject<>(pieceFreightModel));
        });
    }

    /**
     * 查询某个件数运费模板明细 Weight
     */
    public Mono<ReturnObject<WeightFreightModel>> getWeightItemByFreightModelIdRegionId(Long shopId, Long id,Long RegionId) {
        return weightFreightModelRepository.findByFreightModelIdAndRegionId(id,RegionId).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.FREIGHTMODEL_SHOP_NOTFIT));
            }
            List<WeightFreightModelPo> weightFreightModelPos = resOptional.get();
            WeightFreightModel weightFreightModel=new WeightFreightModel(weightFreightModelPos.get(0));
            return Mono.just(new ReturnObject<>(weightFreightModel));
        });
    }

}
