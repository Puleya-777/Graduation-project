package com.example.model.state;

import com.example.model.bo.Comment;
import lombok.Data;

/**
 * 管理员状态VO
 * @author LiangJi3229
 * @date 2020/11/10 18:41
 */
@Data
public class CommentStateVo {
    private Long Code;

    private String name;
    public CommentStateVo(CommentState state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
