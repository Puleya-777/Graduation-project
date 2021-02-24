package demo.address.model.vo;

import demo.address.model.po.AddressPo;
import demo.address.model.po.RegionPo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chei1
 */
@Getter
@Setter
@ToString
public class ReturnAddressVo {
    private Long id;
    private Long customerId;
    private List<RegionPo> regionList;
    private String detail;
    private String consignee;
    private String mobile;
    private Boolean isDefault;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Integer state;

    public ReturnAddressVo(AddressPo addressPo){
        this.id=addressPo.getId();
        this.customerId=addressPo.getCustomerId();
        this.detail=addressPo.getDetail();
        this.consignee=addressPo.getConsignee();
        this.mobile=addressPo.getMobile();
        this.isDefault=addressPo.getIsDefault();
        this.gmtCreate=addressPo.getGmtCreate();
        this.gmtModified=addressPo.getGmtModified();
        this.state=addressPo.getState();
    }
}
