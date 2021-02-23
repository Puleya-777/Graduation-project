package demo.advertise.controller;

import com.example.util.ReturnObject;
import demo.advertise.model.po.AdvertisePo;
import demo.util.OssFileUtil;
import demo.advertise.model.vo.ModifiedAdVo;
import demo.advertise.model.vo.NewAdVo;
import demo.advertise.repository.AdvertiseRepository;
import demo.advertise.service.AdvertiseService;
import demo.util.StateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author chei1
 */
@RestController
@Slf4j
public class AdvertiseController {

    @Resource
    AdvertiseRepository advertiseRepository;
    @Autowired
    AdvertiseService advertiseService;
    @Autowired
    OssFileUtil ossFileUtil;

    @GetMapping("/advertisement/all")
    public Flux getAllAd(){
        return advertiseRepository.findAll();
    }

    /**
     * 获得广告的所有状态
     */
    @GetMapping("/advertisement/states")
    public Mono getAdStates(){
        List<StateVo> list=new ArrayList<>();
        for(Integer a: AdvertisePo.stateMap.keySet()){
            list.add(new StateVo(a,AdvertisePo.stateMap.get(a)));
        }
        return Mono.just(new ReturnObject<>(list));
    }

    /**
     * 管理员设置默认广告
     * TODO 解析判断是否有管理员权限
     */
    @PutMapping("/shops/{did}/advertisement/{id}/default")
    //@Audit
    public Mono setAdDefault(@PathVariable Long did,@PathVariable Long id){
        return advertiseService.setAdDefault(id);
    }

    /**
     * 管理员修改广告内容
     * TODO 解析判断是否有管理员权限
     */
    @PutMapping("/shops/{did}/advertisement/{id}")
    //@Audit
    public Mono modifiedAd(@PathVariable Long did, @PathVariable Long id, @RequestBody ModifiedAdVo vo){
        return advertiseService.modifiedAd(id,vo);
    }

    /**
     * 管理员删除广告
     * TODO 解析判断是否有管理员权限
     */
    @DeleteMapping("/shops/{did}/advertisement/{id}")
    //@Audit
    public Mono deleteAd(@PathVariable Long did, @PathVariable Long id){
        return advertiseService.deleteAd(id);
    }


    @PostMapping("/upload")
    public Mono uploadTest(@RequestParam("file") MultipartFile file) throws IOException {
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
        log.info(sb.toString());
        return ossFileUtil.uploadAliyun(file,sb.toString());
    }

    /**
     * TODO 获取当前时段的广告（时段未划分） 已上架广告
     */
//    @GetMapping("/advertisement/current")


    /**
     *  管理员上传广告图片
     */
    @PostMapping("/shops/{did}/advertisement/{id}/uploadImg")
    public Mono uploadImg(@PathVariable Long did, @PathVariable Long id,@RequestParam("file") MultipartFile file){
        return advertiseService.uploadImg(id,file);
    }

    /**
     *  管理员在广告时段下新建广告
     */
    @PostMapping("/shops/{did}/timesegments/{id}/advertisement")
    public Mono newAdvertise(@PathVariable Long did, @PathVariable Long id, @RequestBody NewAdVo vo){
        return advertiseService.newAdvertise(id,vo);
    }

    /**
     *  管理员添加已有的广告到某个时段下
     */
    @PostMapping("/shops/{did}/timesegments/{tid}/advertisement/{id}")
    public Mono ModifiedTime(@PathVariable Long did, @PathVariable Long id,@PathVariable Long tid){
        return advertiseService.modifiedTime(id,tid);
    }

    /**
     * 管理员上架广告
     * TODO 验证是否为管理员
     */
    @PutMapping("/shops/{did}/advertisement/{id}/onshelves")
    public Mono onShelves(@PathVariable Long did, @PathVariable Long id){
        return advertiseService.onShelves(id);
    }

    /**
     * 管理员下架广告
     * TODO 验证是否为管理员
     */
    @PutMapping("/shops/{did}/advertisement/{id}/offshelves")
    public Mono offShelves(@PathVariable Long did, @PathVariable Long id){
        return advertiseService.offShelves(id);
    }

    /**
     * 管理员审核广告
     * TODO 验证是否为管理员
     */
    @PutMapping("/shops/{did}/advertisement/{id}/audit")
    public Mono auditAd(@PathVariable Long did, @PathVariable Long id){
        return advertiseService.auditAd(id);
    }
    /**
     * 管理员查看某一时间段的广告
     * TODO 验证是否为管理员
     */
    @GetMapping("/shops/{did}/timesegments/{id}/advertisement")
    public Mono getAdByTime(@PathVariable Long did, @PathVariable Long id){
        return advertiseService.getAdByTime(id);
    }


}







