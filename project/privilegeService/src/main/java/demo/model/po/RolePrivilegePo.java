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
@Table("auth_role_privilege")
@Getter
@Setter
@Builder
public class RolePrivilegePo {

    @Id
    private Long id;

    private Long roleId;

    private Long privilegeId;

    private Long creatorId;

    private LocalDateTime gmtCreate;

    private String signature;
}