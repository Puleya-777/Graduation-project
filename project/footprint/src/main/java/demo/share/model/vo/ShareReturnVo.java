package demo.share.model.vo;

import demo.footprint.model.vo.SkuVo;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Data
public class ShareReturnVo {

    private Long id;
    private Long sharerId;
    private Long skuId;
    private SkuVo skuVo;
    private Integer quantity;
    private LocalDateTime gmtCreate;
    private String shareUrl;
}
