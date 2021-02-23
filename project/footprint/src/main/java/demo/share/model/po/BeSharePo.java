package demo.share.model.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Getter
@Setter
@ToString
@Table("be_share")
public class BeSharePo {
    @Id
    private Long id;
    
    private Long goodsSkuId;
    
    private Long sharerId;
    
    private Long shareId;
    
    private Long customerId;
    
    private Long orderId;
    
    private Integer rebate;
    
    private LocalDateTime gmtCreat;
    
    private LocalDateTime gmtModified;
    
    private Long shareActivityId;
}
