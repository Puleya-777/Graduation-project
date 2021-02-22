package demo.address.model.po;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Table("foot_print")
@Getter
@Setter
public class FootPrintPo {
    @Id
    private Long id;

    private Long customerId;

    private Long goodsSkuId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
