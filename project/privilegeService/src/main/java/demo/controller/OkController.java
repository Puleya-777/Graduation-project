package demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chei1
 */
@RestController
public class OkController {
    @RequestMapping("/ok")
    public String ok() {
        return "ok";
    }

}
