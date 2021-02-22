package demo.aftersale.model.vo;

import lombok.Getter;

/**
 * @author chei1
 */
@Getter
public class NewAfterSaleVo {
    private Integer type;
    private Integer quantity;
    private String reason;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
}
