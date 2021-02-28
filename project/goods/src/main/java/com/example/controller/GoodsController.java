package com.example.controller;

import com.example.annotation.LoginUser;
import com.example.model.po.BrandPo;
import com.example.model.po.CategoryPo;
import com.example.model.po.FloatPricePo;
import com.example.model.po.SkuPo;
import com.example.model.state.CommentState;
import com.example.model.state.CommentStateVo;
import com.example.model.state.SpuState;
import com.example.model.state.SpuStateVo;
import com.example.model.vo.*;
import com.example.service.BrandService;
import com.example.service.CategoryService;
import com.example.service.FloatPriceService;
import com.example.service.GoodsService;
import com.example.util.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    FloatPriceService floatPriceService;
    @Autowired
    BrandService brandService;

    @ApiOperation(value = "获得商品SPU的所有状态")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
//            @ApiImplicitParam(name = "id", value = "角色id", required = true, dataType = "Integer", paramType = "path"),
//            @ApiImplicitParam(name = "did", value = "部门id", required = true, dataType = "Integer", paramType = "path")
//
//    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
//            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @GetMapping("/skus/states")
    public Mono<Object> getGoodsSpuStatus(){
        SpuState[] states= SpuState.class.getEnumConstants();
        List<SpuStateVo> SpuStateVos =new ArrayList<SpuStateVo>();
        for(int i=0;i<states.length;i++){
            SpuStateVos.add(new SpuStateVo(states[i]));
        }
        return Mono.just(ResponseUtil.ok(new ReturnObject<List>(SpuStateVos).getData()));
    }

    /**
     * 查询sku
     * @param shopId
     * @param skuSn
     * @param spuId
     * @param spuSn
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/skus")
    public Mono<Object> querySku(@RequestParam Integer shopId,@RequestParam String skuSn,@RequestParam Long spuId,
                                 @RequestParam String spuSn,@RequestParam(required = false,defaultValue = "1") Integer page,
                                 @RequestParam(required = false,defaultValue = "1") Integer pageSize){
        return goodsService.querySku(skuSn,spuId,page,pageSize).map(Common::getPageRetObject);
    }

    /**
     * 获得sku的详细信息
     * @param id
     * @return
     */
    @GetMapping("/skus/{id}")
    public Mono<Object> getSkuInfo(@PathVariable String id){
        return goodsService.getSkuInfoById(Long.valueOf(id)).map(returnObject -> {
            if(returnObject.getCode()== ResponseCode.OK){
                return Common.getRetObject(returnObject);
            }
            return Common.getRetObject(returnObject);
        });
    }

    /**
     * 管理员添加新的SKU到SPU里
     * @param userId
     * @param shopId
     * @param id
     * @param skuVo
     * @return
     */
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 901, message = "商品规格重复")
    })
    @PostMapping("/shops/{shopId}/spus/{id}/skus")
    public Mono<Object> addSkuToSpu(@LoginUser Long userId,@PathVariable Integer shopId,@PathVariable Long id,
                                    @RequestBody SkuVo skuVo){
        SkuPo skuPo=new SkuPo(skuVo);
        skuPo.setGoodsSpuId(id);
        return goodsService.addSkuToSpu(shopId,id,skuPo).map(Common::getRetObject);

    }

    /**
     * sku上传图片,如果该sku有图片，需修改该sku的图片，并删除图片文件
     * @param userId
     * @param shopId
     * @param skuId
     * @param img
     * @return
     */
    @PostMapping("/shops/{shopId}/skus/{id}/uploadImg")
    public Mono<Object> updatePicToSku(@LoginUser Long userId, @RequestParam Integer shopId, @RequestParam Long skuId,
                                       @RequestParam("file") MultipartFile img){
        return goodsService.updatePicToSku(skuId,img).map(Common::getRetObject);
    }

    /**
     * 管理员或店家逻辑删除SKU
     * @param userId
     * @param skuId
     * @param shopId
     * @return
     */
    @DeleteMapping("/shops/{shopId}/skus/{id}")
    public Mono<Object> deleteSku(@LoginUser Long userId,@PathVariable Long skuId,@PathVariable Integer shopId){
        return goodsService.deleteSku(skuId).map(returnObject -> ResponseUtil.ok());
    }

    /**
     * 管理员或店家修改SKU信息
     * @param userId
     * @param shopId
     * @param skuId
     * @param skuVo
     * @return
     */
    @PutMapping("/shops/{shopId}/skus/{id}")
    public Mono<Object> putSku(@LoginUser Long userId, @PathVariable Integer shopId,
                               @PathVariable Long skuId, @RequestBody SkuVo skuVo){
        return goodsService.putSku(skuId,skuVo).map(returnObject -> ResponseUtil.ok());
    }

    /**
     * 根据种类ID获取商品下一级分类信息
     * @param id
     * @return
     */
    @GetMapping("/categories/{id}/subcategories")
    public Mono<Object> queryCategoryRelation(@PathVariable Long id){
        return goodsService.queryCategoryRelation(id).map(Common::getListRetObject);
    }

    /**
     * 管理员新增商品类目
     * @param userId
     * @param shopId
     * @param id
     * @param categoryDetail
     * @return
     */
    @PostMapping("/shops/{shopId}/categories/{id}/subcategories")
    public Mono<Object> addGoodsCategory(@LoginUser Long userId,@PathVariable Integer shopId,
                                         @PathVariable Long id,@RequestBody CategoryInfoVo categoryDetail){
        return categoryService.addGoodsCategory(id,categoryDetail).map(Common::getRetObject);
    }

    /**
     * 管理员修改商品类目信息
     * @param userId
     * @param shopId
     * @param id
     * @param vo
     * @return
     */
    @PutMapping("/shops/{shopId}/categories/{id}")
    public Mono<Object> modifyCategory(@LoginUser Long userId,@PathVariable Integer shopId,
                                       @PathVariable Long id,@RequestBody CategoryInfoVo vo){
        return categoryService.modifyCategory(id,vo).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 管理员删除商品类目信息
     * @param userId
     * @param shopId
     * @param id
     * @return
     */
    @DeleteMapping("/shops/{shopId}/categories/{id}")
    public Mono<Object> deleteCategory(@LoginUser Integer userId,@PathVariable Integer shopId,
                                       @PathVariable Long id){
        return categoryService.deleteCategory(id).map(returnObject -> ResponseUtil.ok());
    }

    /**
     * 查看一条商品SPU的详细信息（无需登录）
     * @param id
     * @return
     */
    @GetMapping("/spus/{id}")
    public Mono<Object> getSpuDetail(@PathVariable Long id){
        return goodsService.getSpuInfoById(id).map(Common::getRetObject);
    }

    /**
     * 查看一条分享商品SPU的详细信息（需登录）
     * @param userId
     * @param sid
     * @param id
     * @return
     */
    @GetMapping("/share/{sid}/skus/{id}")
    public Mono<Object> getShareGoodsSpuDetail(@LoginUser Long userId,@PathVariable Integer sid,
                                               @PathVariable Long id){
        return goodsService.getSpuInfoById(id).map(Common::getRetObject);
    }

    /**
     * 店家新建商品SPU
     * @param userId
     * @param id
     * @param spuVo
     * @return
     */
    @PostMapping("/shops/{id}/spus")
    public Mono<Object> newSpu(@LoginUser Long userId, @PathVariable Long id, @RequestBody SpuVo spuVo){
        return goodsService.newSpu(id,spuVo).map(Common::getRetObject);
    }

    /**
     * 店家修改商品SPU
     * @param userId
     * @param shopId
     * @param id
     * @param spuVo
     * @return
     */
    @PutMapping("/shops/{shopId}/spus/{id}")
    public Mono<Object> modifySpu(@LoginUser Long userId,@PathVariable Long shopId,
                                  @PathVariable Long id,@RequestBody SpuVo spuVo){
        return goodsService.modifySpu(id,spuVo).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 店家逻辑删除商品SPU
     * @param userId
     * @param shopId
     * @param id
     * @return
     */
    @DeleteMapping("/shops/{shopId}/spus/{id}")
    public Mono<Object> deleteSpu(@LoginUser Long userId,@PathVariable Long shopId,@PathVariable Long id){
        return goodsService.deleteSpu(id).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 店家商品上架
     * @param userId
     * @param shopId
     * @param id
     * @return
     */
    @PutMapping("/shops/{shopId}/skus/{id}/onshelves")
    public Mono<Object> goodsOnShelves(@LoginUser Long userId,@PathVariable Integer shopId,@PathVariable Long id){
        return goodsService.modifyGoodsState(id,0).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 店家商品下架
     * @param userId
     * @param shopId
     * @param id
     * @return
     */
    @PutMapping("/shops/{shopId}/skus/{id}/offshelves")
    public Mono<Object> goodsOffShelves(@LoginUser Long userId,@PathVariable Integer shopId,@PathVariable Long id){
        return goodsService.modifyGoodsState(id,1).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 管理员新增商品价格浮动
     * @param userId
     * @param shopId
     * @param id  sku id
     * @param floatPriceVo
     * @return
     */
    @PostMapping("/shops/{shopId}/skus/{id}/floatPrices")
    public Mono<Object> addFloatingPrice(@LoginUser Long userId, @PathVariable Integer shopId,
                                         @PathVariable Long id, @RequestBody FloatPriceVo floatPriceVo){
        FloatPricePo floatPricePo=new FloatPricePo(floatPriceVo,id,userId);

        return floatPriceService.addFloatingPrice(floatPricePo).map(Common::getRetObject);
    }

    /**
     * 管理员失效商品价格浮动
     * @param userId
     * @param shopId
     * @param id
     * @return
     */
    @DeleteMapping("/shops/{shopId}/floatPrices/{id}")
    public Mono<Object> invalidFloatPrice(@LoginUser Long userId,@PathVariable Integer shopId,@PathVariable Long id){
        return floatPriceService.invalidFloatPrice(userId,id).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 管理员新增品牌
     * @param userId
     * @param id    店铺id
     * @param brandVo
     * @return
     */
    @PostMapping("/shops/{id}/brands")
    public Mono<Object> addBrand(@LoginUser Long userId, @PathVariable Integer id, @RequestBody BrandVo brandVo){
        BrandPo brandPo=new BrandPo(brandVo);
        return brandService.addBrand(brandPo).map(Common::getRetObject);
    }


    /**
     * 上传图片,如果该品牌有图片，需修改该品牌的图片，并删除图片文件
     * @param userId
     * @param shopId
     * @param id
     * @param img
     * @return
     */
    @PostMapping("/shops/{shopId}/brands/{id}/uploadImg")
    public Mono uploadBrandImg(@LoginUser Long userId,@PathVariable Long shopId,
                                       @PathVariable Long id,@RequestParam("file") MultipartFile img){
        return brandService.uploadBrandImg(id,img);
    }

    /**
     * 查看所有品牌
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/brands")
    public Mono<Object> queryBrand(@RequestParam(required = false,defaultValue = "1") Integer page,
                                   @RequestParam(required = false,defaultValue = "1") Integer pageSize){
        return brandService.queryAllBrand(page,pageSize).map(Common::getPageRetObject);
    }

    /**
     * 管理员修改品牌
     * @param userId
     * @param shopId
     * @param id
     * @param brandVo
     * @return
     */
    @PutMapping("/shops/{shopId}/brands/{id}")
    public Mono<Object> modifyBrand(@LoginUser Long userId,@PathVariable Integer shopId,
                                    @PathVariable Long id,@RequestBody BrandVo brandVo){
        return brandService.modifyBrand(id,brandVo).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 管理员删除品牌
     * @param userId
     * @param shopId
     * @param id
     * @return
     */
    @DeleteMapping("/shops/{shopId}/brands/{id}")
    public Mono<Object> deleteBrand(@LoginUser Long userId,@PathVariable Integer shopId,
                                    @PathVariable Long id){
        return brandService.deleteBrand(id).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 将SPU加入分类
     * 如果该SPU有分类，需修改该SPU的分类，控制只能加入二级分类
     * @param userId
     * @param shopId
     * @param spuId
     * @param id  分类 id
     * @return
     */
    @PostMapping("/shops/{shopId}/spus/{spuId}/categories/{id}")
    public Mono<Object> addSpuCategory(@LoginUser Long userId,@PathVariable Integer shopId,
                                       @PathVariable Long spuId,@PathVariable Long id){
        return goodsService.addSpuCategory(spuId,id).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 将SPU移出分类
     * @param userId
     * @param shopId
     * @param spuId
     * @param id
     * @return
     */
    @DeleteMapping("/shops/{shopId}/spus/{spuId}/categories/{id}")
    public Mono<Object> removeSpuCategory(@LoginUser Long userId,@PathVariable Long shopId,
                                       @PathVariable Long spuId,@PathVariable Long id){
        return goodsService.addSpuCategory(spuId,null).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 将SPU加入品牌"
     * description: "如果该SPU有品牌，需修改该SPU的品牌"
     * @param userId
     * @param shopId
     * @param spuId
     * @param id
     * @return
     */
    @PostMapping("/shops/{shopId}/spus/{spuId}/brands/{id}")
    public Mono<Object> addSpuBrand(@LoginUser Long userId,@PathVariable Long shopId,
                                    @PathVariable Long spuId,@PathVariable Long id){
        return goodsService.addSpuBrand(spuId,id).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }

    /**
     * 将SPU移出品牌
     * description: "如果该SPU变成无品牌商品"
     * @param userId
     * @param shopId
     * @param spuId
     * @param id
     * @return
     */
    @DeleteMapping("/shops/{shopId}/spus/{spuId}/brands/{id}")
    public Mono<Object> removeSpuBrand(@LoginUser Long userId,@PathVariable Long shopId,
                                    @PathVariable Long spuId,@PathVariable Long id){
        return goodsService.addSpuBrand(spuId,null).map(returnObject -> {
            if(returnObject.getCode()==ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(returnObject.getCode());
            }
        });
    }


    @GetMapping("/shops/spus")
    public Mono<Object> getAllSpuInShop(@RequestParam Long shopId,
                                        @RequestParam(required = false,defaultValue = "1") Integer page,
                                        @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        return goodsService.getAllSpuInShop(shopId,page,pageSize).map(Common::getPageRetObject);
    }
}
