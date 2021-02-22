package demo.address.model.po;

import demo.address.model.vo.NewAddressVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author chei1
 */
@ToString
@Getter
@Setter
@Table("address")
public class AddressPo {
    @Id
    private Long id;

    private Long customerId;

    private Long regionId;

    private String detail;

    private String consignee;

    private String mobile;

    @Column("be_default")
    private Boolean isDefault;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Integer state;

    public void trans(NewAddressVo vo){
        this.setConsignee(vo.getConsignee());
        this.setDetail(vo.getDetail());
        this.setRegionId(vo.getRegionId());
        this.setMobile(vo.getMobile());
        this.setGmtCreate(LocalDateTime.now());
        this.setGmtModified(LocalDateTime.now());
        this.setIsDefault(false);
        this.setState(0);
    }
    public void update(NewAddressVo vo){
        this.setConsignee(vo.getConsignee());
        this.setDetail(vo.getDetail());
        this.setRegionId(vo.getRegionId());
        this.setMobile(vo.getMobile());
        this.setGmtModified(LocalDateTime.now());
    }
}
