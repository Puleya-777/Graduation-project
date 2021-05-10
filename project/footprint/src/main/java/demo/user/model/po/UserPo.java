package demo.user.model.po;

import demo.user.model.vo.ModifiedUserVo;
import demo.user.model.vo.RegisteredVo;
import lombok.Data;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

/**
 * @author chei1
 */
@Data
@Table("customer")
public class UserPo {

    @Id
    private Long id;
    private String userName;
    private String password;
    private String realName;
    private Integer gender;
    private LocalDate birthday;
    private Integer point;
    private String mobile;
    private String email;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Integer beDeleted;
    private Integer state;
    public static HashMap<Integer,String> stateMap =new HashMap<Integer,String>();

    static {
        stateMap.put(1, "正常");
        stateMap.put(2, "封禁");
        stateMap.put(3, "废弃");
    }

    public void registered(RegisteredVo vo){
        this.userName=vo.getUserName();
        this.password= getMD5Str(vo.getPassword());
        this.realName=vo.getRealName();
        this.gender=vo.getGender();
        this.birthday=LocalDate.parse(vo.getBirthday());
        this.point=0;
        this.mobile=vo.getMobile();
        this.email=vo.getEmail();
        this.gmtCreate=LocalDateTime.now();
        this.gmtModified=LocalDateTime.now();
        this.beDeleted=0;
        this.state=1;
    }
    public void modified(ModifiedUserVo vo){
        this.realName=vo.getRealName();
        this.gender=vo.getGender();
        this.birthday=LocalDate.parse(vo.getBirthday());
        this.gmtModified=LocalDateTime.now();
    }

    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest  = md5.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        String md5Str = new BigInteger(1, digest).toString(16);
        return md5Str;
    }
}
