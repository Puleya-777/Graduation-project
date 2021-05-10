package demo.footprint.controller;

import demo.footprint.service.FootprintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@RestController
@Slf4j
public class FootprintController {
    @Autowired
    FootprintService footprintService;

    @GetMapping("/shops/{did}/footprints")
    public Mono getFootprint(@PathVariable Long did, @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(required = false) Long userId,
                             @RequestParam(required = false) LocalDateTime beginTime,@RequestParam(required = false) LocalDateTime endTime){
        if(userId==null){
            return footprintService.getAllFootprint(page,pageSize);
        }else {
            return footprintService.getFootprint(userId,page,pageSize);
        }
    }
}
