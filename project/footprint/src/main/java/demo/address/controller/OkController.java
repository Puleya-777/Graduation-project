package demo.address.controller;



import demo.footprint.repository.FootprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
@RestController
public class OkController {

    @Autowired
    FootprintRepository footPrintRepository;

    @RequestMapping("ok")
    public Mono<String> ok(){
        return Mono.just("ok");
    }

//    @RequestMapping("insert")
//    public Mono insertTest(){
//        FootPrintPo footPrintPo=new FootPrintPo();
//        footPrintPo.setCustomerId(1L);
//        footPrintPo.setGoodsSkuId(2L);
//        footPrintPo.setGmtCreate(LocalDateTime.now());
//        footPrintPo.setGmtModified(LocalDateTime.now());
//        return footPrintRepository.save(footPrintPo);
//    }
}
