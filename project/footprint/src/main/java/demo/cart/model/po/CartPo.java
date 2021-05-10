package demo.cart.model.po;

import demo.cart.model.vo.NewCartVo;
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
@Table("shopping_cart")
public class CartPo {
    @Id
    private Long id;

    private Long customerId;

    private Long goodsSkuId;

    private Integer quantity;

    private Long price;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public void trans(Long customerId,NewCartVo vo){
        this.customerId=customerId;
        this.goodsSkuId=vo.getGoodsSkuId();
        this.quantity=vo.getQuantity();
        this.gmtCreate=LocalDateTime.now();
        this.gmtModified=LocalDateTime.now();
    }

}
