package demo.model.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Table("auth_new_user")
@Getter
@Setter
@Builder
public class NewUserPo {

    @Id
    private Long id;


    private String userName;

    private String password;

    private String mobile;

    private String email;


    private String name;


    private String avatar;


    private String openId;


    private Long departId;


    private LocalDateTime gmtCreate;

}