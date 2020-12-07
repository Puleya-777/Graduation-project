package demo.model.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
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
    @Column("user_name")
    private String userName;
    @Column("password")
    private String password;
    @Column("mobile")
    private String mobile;
    @Column("email")
    private String email;
    @Column("name")
    private String name;
    @Column("avatar")
    private String avatar;
    @Column("open_id")
    private String openId;
    @Column("depart_id")
    private Long departId;
    @Column("gmt_create")
    private LocalDateTime gmtCreate;

}