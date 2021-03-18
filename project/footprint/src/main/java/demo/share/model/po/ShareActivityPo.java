package demo.share.model.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author chei1
 */
@Getter
@Setter
@ToString
@Table("share_activity")
public class ShareActivityPo {
    @Id
    private Long id;

    private Long shopId;

    private Long goodsSkuId;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private String strategy;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Integer state;

    private Integer quantity;

    public static HashMap<Integer,String> stateMap =new HashMap<Integer,String>();

    static {
        stateMap.put(1, "待发布");
        stateMap.put(2, "已发布");
        stateMap.put(3, "已下线");
    }
}
