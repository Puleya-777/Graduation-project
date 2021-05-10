package demo.footprint.model.vo;

import demo.footprint.model.po.FootprintPo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Data
@NoArgsConstructor
public class FootprintVo {
    private Long id;
    private SkuVo goodsSku;
    private LocalDateTime gmtCreate;
    private Long skuId;

    public FootprintVo(FootprintPo po){
        this.setId(po.getId());
        this.setSkuId(po.getGoodsSkuId());
        this.setGmtCreate(po.getGmtCreate());
    }
}
