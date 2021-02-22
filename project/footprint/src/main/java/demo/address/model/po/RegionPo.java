package demo.address.model.po;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Getter
@Setter
@Table("region")
public class RegionPo {
    @Id
    private Long id;

    private Long pid;

    private String name;

    private Long postalCode;

    private Integer state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
