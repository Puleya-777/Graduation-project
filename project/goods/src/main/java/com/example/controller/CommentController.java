package com.example.controller;

import com.example.annotation.LoginUser;
import com.example.model.bo.Comment;
import com.example.model.state.CommentState;
import com.example.model.vo.CommentVo;
import com.example.model.state.CommentStateVo;
import com.example.service.CommentService;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ResponseUtil;
import com.example.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    //TODO
    @GetMapping("/comments/states")
    public Mono<Object> getcommentState(){
        CommentState[] states= CommentState.class.getEnumConstants();
        List<CommentStateVo> commentStateVos =new ArrayList<CommentStateVo>();
        for(int i=0;i<states.length;i++){
            commentStateVos.add(new CommentStateVo(states[i]));
        }
        return Mono.just(ResponseUtil.ok(new ReturnObject<List>(commentStateVos).getData()));
    }

    @PostMapping("/orderitems/{id}/comments")
    public Mono<Object> addSkuComment(@LoginUser Long userId, @PathVariable Long orderItemId,
                                      @RequestBody CommentVo commentVo){
        return commentService.addSkuComment(orderItemId,commentVo).map(Common::getRetObject);
    }

    @GetMapping("/skus/{id}/comments")
    public Mono<Object> getSkuComment(@PathVariable Long id,
                                      @RequestParam(required = false,defaultValue = "1") Integer page,
                                      @RequestParam(required = false,defaultValue = "1") Integer pageSize){
        return commentService.getSkuComment(id,page,pageSize).map(Common::getPageRetObject);
    }

    @PutMapping("/shops/{did}/comments/{id}/confirm")
    public Mono<Object> auditComment(@LoginUser Long userId,@PathVariable Long did,
                                     @PathVariable Long id,@RequestBody CommentVo commentVo){
        return commentService.auditComment(id,commentVo).map(ret->{
            if(ret.getCode()== ResponseCode.OK){
                return ResponseUtil.ok();
            }else {
                return ResponseUtil.fail(ResponseCode.INTERNAL_SERVER_ERR);
            }
        });
    }

    @GetMapping("comments")
    public Mono<Object> showComment(@LoginUser Long userId,@RequestParam(required = false,defaultValue = "1") Integer page,
                                    @RequestParam(required = false,defaultValue = "1") Integer pageSize){
        return commentService.showComment(userId,page,pageSize).map(Common::getPageRetObject);
    }

    @GetMapping("/shops/{id}/comments/all")
    public Mono<Object> showUnAuditComments(@LoginUser Long userId,@PathVariable Long id,
                                            @RequestParam(required = false) Integer state,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer pageSize){
        return commentService.showUnAuditComments(userId,id,state,page,pageSize)
                .map(Common::getPageRetObject);
    }

}
