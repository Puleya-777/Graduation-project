package demo.advertise.model.po;

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
@Table("time_segment")
public class TimeSegmentPo {
    @Id
    private Long id;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Integer type;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;


}
