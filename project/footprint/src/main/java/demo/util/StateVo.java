package demo.util;

import lombok.Getter;
import lombok.Setter;

/**
 * @author chei1
 */
@Getter
@Setter
public class StateVo {
    private Integer code;
    private String name;

    public StateVo(Integer a,String b){
        this.code=a;
        this.name=b;
    }
}
