package demo.favorite.model.vo;

import demo.favorite.model.po.FavoritePo;
import demo.footprint.model.po.FootprintPo;
import demo.footprint.model.vo.SkuVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@Data
@NoArgsConstructor
public class FavoriteVo {
    private Long id;
    private SkuVo goodsSku;
    private LocalDateTime gmtCreate;
    private Long skuId;

    public FavoriteVo(FavoritePo po){
        this.setId(po.getId());
        this.setSkuId(po.getGoodsSkuId());
        this.setGmtCreate(po.getGmtCreate());
    }
}
