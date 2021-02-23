package demo.advertise.model.po;

import demo.advertise.model.vo.ModifiedAdVo;
import demo.advertise.model.vo.NewAdVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author chei1
 */
@Getter
@Setter
@ToString
@Table("advertisement")
public class AdvertisePo {
    @Id
    private Long id;

    private Long segId;

    private String link;

    private String content;

    @Column("image_url")
    private String imagePath;

    private int state;

    private int weight;

    private LocalDate beginDate;

    private LocalDate endDate;

    @Column("repeats")
    private Boolean repeat;

    private String message;

    private Boolean beDefault;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public static HashMap<Integer,String> stateMap =new HashMap<Integer,String>();

    static {
        stateMap.put(1,"新增");
        stateMap.put(2,"审核通过");
        stateMap.put(3,"上架");
        stateMap.put(4,"下架");
        stateMap.put(5,"失效");
    }

    public void trans(ModifiedAdVo vo){
        this.content=vo.getContent()==null?this.content:vo.getContent();
        this.beginDate=vo.getBeginDate()==null?this.beginDate: LocalDate.parse(vo.getBeginDate());
        this.endDate=vo.getEndDate()==null?this.endDate: LocalDate.parse(vo.getEndDate());
        this.weight=vo.getWeight()==null?this.weight:vo.getWeight();
        this.repeat=vo.getRepeat()==null?this.repeat:vo.getRepeat();
        this.link=vo.getLink()==null?this.link:vo.getLink();
        this.setGmtModified(LocalDateTime.now());
    }

    public void newPo(NewAdVo vo){
        this.link=vo.getLink();
        this.content=vo.getContent();
        this.weight=vo.getWeight();
        this.beginDate=LocalDate.parse(vo.getBeginDate());
        this.endDate=LocalDate.parse(vo.getEndDate());
        this.repeat=vo.getRepeat();
        this.gmtCreate=LocalDateTime.now();
        this.gmtModified=LocalDateTime.now();

    }

}
