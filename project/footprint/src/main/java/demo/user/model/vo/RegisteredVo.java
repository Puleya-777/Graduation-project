package demo.user.model.vo;

import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * @author chei1
 */
@Data
public class RegisteredVo {


    @Pattern(regexp="[+]?[0-9*#]+",message="手机号格式不正确")
    private String mobile;
    @Email(message = "email格式不正确")
    private String email;
    @Length(min=6,message = "用户名长度过短")
    private String userName;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",message = "密码格式不正确，请包含大小写字母数字及特殊符号")
    private String password;
    private String realName;
    private Integer gender;
    private String birthday;
}
