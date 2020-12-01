package demo.model.po;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Table("auth_user")
@Getter
@Setter
@Builder
public class UserPo {

    @Id
    private Long id;


    private String userName;


    private String password;


    private String mobile;


    private Byte mobileVerified;


    private String email;


    private Byte emailVerified;


    private String name;


    private String avatar;


    private LocalDateTime lastLoginTime;


    private String lastLoginIp;


    private String openId;


    private Byte state;


    private Long departId;


    private LocalDateTime gmtCreate;


    private LocalDateTime gmtModified;


    private String signature;


    private Long creatorId;

}