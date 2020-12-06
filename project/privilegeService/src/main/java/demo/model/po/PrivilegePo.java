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
@Table("auth_privilege")
@Getter
@Setter
@Builder
public class PrivilegePo {

    @Id
    private Long id;

    private String name;

    private String url;

    private Byte requestType;

    private String signature;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}