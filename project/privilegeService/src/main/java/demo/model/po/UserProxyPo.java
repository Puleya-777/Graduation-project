package demo.model.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Table("auth_user_proxy")
@Getter
@Setter
@Builder
public class UserProxyPo {

    @Id
    private Long id;

    private Long userAId;

    private Long userBId;

    private LocalDateTime beginDate;

    private LocalDateTime endDate;

    private LocalDateTime gmtCreate;

    private String signature;

    private Byte valid;

    private Long departId;

    public Long getId() {
        return id;
    }


}