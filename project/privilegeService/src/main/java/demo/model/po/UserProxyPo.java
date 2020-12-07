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
@Table("auth_user_proxy")
@Getter
@Setter
@Builder
public class UserProxyPo {

    @Id
    private Long id;
    @Column
    private Long userAId;
    @Column
    private Long userBId;
    @Column
    private LocalDateTime beginDate;
    @Column
    private LocalDateTime endDate;
    @Column
    private LocalDateTime gmtCreate;
    @Column
    private String signature;
    @Column
    private Byte valid;
    @Column
    private Long departId;



}