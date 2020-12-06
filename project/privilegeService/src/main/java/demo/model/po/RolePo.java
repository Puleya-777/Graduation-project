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
@Table("auth_role")
@Getter
@Setter
@Builder
public class RolePo {

    @Id
    private Long id;

    private String name;

    private Long creatorId;

    private String descr;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long departId;

}