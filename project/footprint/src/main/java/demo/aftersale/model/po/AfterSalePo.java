package demo.aftersale.model.po;


import demo.advertise.model.vo.ModifiedAdVo;
import demo.aftersale.model.vo.*;
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
        stateMap.put(2,"店家通过");
        stateMap.put(3,"买家已寄出");
        stateMap.put(4,"店家确认收到");
        stateMap.put(5,"店家已寄出");
        stateMap.put(6,"店家不通过");
        stateMap.put(7,"售后单结束");
        stateMap.put(8,"买家取消");
    }

    /**
     *  TODO 这个orderId可能是去其他模块查得来的
     */
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
        this.beDeleted=false;
    }
    public void modifiedPo(ModifiedAfterSaleVo vo){
        this.quantity=vo.getQuantity()==null?this.quantity:vo.getQuantity();
        this.reason=vo.getReason()==null?this.reason:vo.getReason();
        this.regionId=vo.getRegionId()==null?this.regionId:vo.getRegionId();
        this.detail=vo.getDetail()==null?this.detail:vo.getDetail();
        this.consignee=vo.getConsignee()==null?this.consignee:vo.getConsignee();
        this.mobile=vo.getMobile()==null?this.mobile:vo.getMobile();
    }

    public void adminConfirm(AdminConfirmVo vo){
        if(vo.getConfirm()){
            this.state=2;
        }else{
            this.state=6;
        }
        this.refund=vo.getPrice();
        this.conclusion=vo.getConclusion();
        this.type=vo.getType();
    }

    public void adminReceive(AdminReceiveVo vo){
        if(vo.getConfirm()){
            this.state=4;
        }
        this.conclusion=vo.getConclusion();
    }

    public void adminDeliver(AdminDeliverVo vo){
        this.shopLogSn=vo.getShopLogSn();
        this.state=5;
    }

}
