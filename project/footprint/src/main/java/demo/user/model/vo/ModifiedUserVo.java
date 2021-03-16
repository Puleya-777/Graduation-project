package demo.user.model.vo;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author chei1
 */
@Getter
public class ModifiedUserVo {
    private String realName;
    private Integer gender;
    private String birthday;
}
