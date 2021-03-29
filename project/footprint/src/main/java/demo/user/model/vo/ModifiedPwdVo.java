package demo.user.model.vo;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Pattern;

/**
 * @author chei1
 */
@Data
public class ModifiedPwdVo {
    private String captcha;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",message = "密码格式不正确，请包含大小写字母数字及特殊符号")
    private String newPassword;
}
