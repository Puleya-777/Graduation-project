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
@Table("auth_user_role")
@Getter
@Setter
@Builder
public class UserRolePo {
   @Id
    private Long id;


    private Long roleId;


    private Long userId;


    private Long creatorId;


    private String signature;


    private LocalDateTime gmtCreate;

}