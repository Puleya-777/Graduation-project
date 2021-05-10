package com.example.service;

import com.example.model.VoObject;
import com.example.model.bo.Brand;
import com.example.model.po.BrandPo;
import com.example.model.vo.BrandVo;
import com.example.repository.BrandRepository;
import com.example.util.CommonUtil;
import com.example.util.OssFileUtil;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Resource
    BrandRepository brandRepository;
    @Autowired
    OssFileUtil ossFileUtil;
    @Autowired
    CommonUtil commonUtil;

    public Mono<ReturnObject> addBrand(BrandPo brandPo) {
        return brandRepository.save(brandPo).map(Brand::new).map(ReturnObject::new);
    }

    public Mono<ReturnObject> queryAllBrand(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Flux<BrandPo> brandPoFlux=brandRepository.findAll();
        return brandPoFlux.collect(Collectors.toList())
                .map(list->commonUtil.listToPage(list,page,pageSize))
                .map(pageInfo -> new ReturnObject<>(pageInfo));
//        Mono<List<VoObject>> ret=brandPoFlux.map(Brand::new).collect(Collectors.toList());
//        Mono<List<BrandPo>> brandPos=brandPoFlux.collect(Collectors.toList());
//
//        return Mono.zip(brandPos,ret).map(tuple->{
//            PageInfo<BrandPo> privPoPage = PageInfo.of(tuple.getT1());
//            PageInfo<VoObject> privPage = new PageInfo<>(tuple.getT2());
//            privPage.setPages(privPoPage.getPages());
//            privPage.setPageNum(privPoPage.getPageNum());
//            privPage.setPageSize(privPoPage.getPageSize());
//            privPage.setTotal(privPoPage.getTotal());
//            return new ReturnObject<>(privPage);
//        });
    }

    public Mono<ReturnObject> modifyBrand(Long id, BrandVo brandVo) {
        return brandRepository.findById(id).defaultIfEmpty(new BrandPo())
                .flatMap(brandPo -> {
                    if(brandPo.getId()==null){
                        return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }else{
                        brandPo.setFromVo(brandVo);
                        return brandRepository.save(brandPo);
                    }
                }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> deleteBrand(Long id) {
        return brandRepository.findById(id).defaultIfEmpty(new BrandPo())
                .flatMap(brandPo -> {
                    if(brandPo.getId()==null){
                        return Mono.just(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }else{
                        return brandRepository.deleteBrandPoById(id);
                    }
                }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> uploadBrandImg(Long id, MultipartFile file) {
        return brandRepository.findById(id).flatMap(brandPo -> {
            String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            Random random=new Random();
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<10;i++){
                int number=random.nextInt(62);
                sb.append(str.charAt(number));
            }
            int begin = file.getOriginalFilename().indexOf(".");
            int last = file.getOriginalFilename().length();
            sb.append(file.getOriginalFilename(), begin, last);
            String filename=sb.toString();
            try {
                return ossFileUtil.uploadAliyun(file,filename).flatMap(url->{
                    brandPo.setImageUrl(url);
                    return brandRepository.save(brandPo).map(a->{
                        if(a!=null) {
                            return new ReturnObject<>();
                        }
                        return null;
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
