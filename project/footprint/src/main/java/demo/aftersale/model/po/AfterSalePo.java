package demo.aftersale.model.po;


import demo.aftersale.model.vo.NewAfterSaleVo;
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
@Table("aftersale_service")
public class AfterSalePo {
    @Id
    private Long id;

    private Long orderId;

    private Long orderItemId;

    private Long customerId;

    private Long shopId;

    private String serviceSn;

    private Integer type;

    private String reason;

    private String conclusion;

    private Long refund;

    private Integer quantity;

    private Long regionId;

    private String detail;

    private String consignee;

    private String mobile;

    private String customerLogSn;

    private String shopLogSn;

    private Integer state;

    private Boolean beDeleted;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public static HashMap<Integer,String> stateMap =new HashMap<Integer,String>();

    static {
        stateMap.put(1,"新增");
        stateMap.put(2,"管理员通过");
        stateMap.put(3,"买家已寄出");
        stateMap.put(4,"店家确认收到");
        stateMap.put(5,"店家已寄出");
        stateMap.put(6,"管理员不通过");
        stateMap.put(7,"售后单结束");
    }

    public void newPo(Long orderItemId, Long customerId, NewAfterSaleVo vo){
        //this.orderId=
        this.orderItemId=orderItemId;
        this.customerId=customerId;
        this.type=vo.getType();
        this.quantity=vo.getQuantity();
        this.reason=vo.getReason();
        this.detail=vo.getDetail();
        this.consignee=vo.getConsignee();
        this.mobile=vo.getMobile();
        this.gmtCreate=LocalDateTime.now();
        this.gmtModified=LocalDateTime.now();
        this.state=1;
    }


}
