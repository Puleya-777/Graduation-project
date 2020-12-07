package demo.Controller;

import demo.repository.NewUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chei1
 */
@RestController
@Slf4j
public class OkController {
    @Autowired
    NewUserRepository newUserRepository;

    @RequestMapping("/ok")
    public String ok() {
//
//        newUserRepository.save(NewUserPo.builder().mobile("1234567890").email("123@qq.com").userName("chhhh")
//                .password("root").build()).block();
        return "ok";
    }

}
