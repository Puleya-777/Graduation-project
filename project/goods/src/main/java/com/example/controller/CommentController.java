package com.example.controller;

import com.example.annotation.LoginUser;
import com.example.model.vo.CommentVo;
import com.example.service.CommentService;
import com.example.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("/comments/states")
    public Mono<Object> getcommentState(){
        return null;
    }

    @PostMapping("/orderitems/{id}/comments")
    public Mono<Object> addSkuComment(@LoginUser Long userId, @PathVariable Long orderItemId,
                                      @RequestBody CommentVo commentVo){
        return commentService.addSkuComment(orderItemId,commentVo).map(Common::getRetObject);
    }

    @GetMapping("/skus/{id}/comments")
    public Mono<Object> getSkuComment(@PathVariable Long id,@RequestParam Integer page,
                                      @RequestParam Integer pageSize){
        return commentService.getSkuComment(id,page,pageSize).map(Common::getPageRetObject);
    }

    @PutMapping("/shops/{did}/comments/{id}/confirm")
    public Mono<Object> auditComment(@LoginUser Long userId,@PathVariable Long did,
                                     @PathVariable Long id,@RequestBody CommentVo commentVo){
        return commentService.auditComment(id,commentVo).map(Common::getRetObject);
    }

    @GetMapping("comments")
    public Mono<Object> showComment(@LoginUser Long userId,@RequestParam Integer page,
                                    @RequestParam Integer pageSize){
        return commentService.showComment(userId,page,pageSize).map(Common::getRetObject);
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
