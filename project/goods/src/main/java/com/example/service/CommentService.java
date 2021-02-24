package com.example.service;

import com.example.model.VoObject;
import com.example.model.bo.Comment;
import com.example.model.po.CommentPo;
import com.example.model.vo.CommentVo;
import com.example.repository.CommentRepository;
import com.example.util.NacosHelp;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    //TODO 查询user模块用id查user，这些函数都需要

    @Resource
    CommentRepository commentRepository;
    @Autowired
    NacosHelp nacosHelp;

    //TODO 要查询下订单那边用户是否买了该商品，没买要返回903 用户没有购买此商品
    public Mono<ReturnObject> addSkuComment(Long orderItemId, CommentVo commentVo) {
        CommentPo commentPo=new CommentPo(commentVo);
        return commentRepository.save(commentPo).map(Comment::new).map(comment -> {
            comment.setCustomer(nacosHelp.findUserById(comment.getCustomer().getId()));
            return comment;
        }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> getSkuComment(Long id, Integer page, Integer pageSize) {
        Mono<List<Comment>> comments=commentRepository.findAllByGoodsSkuId(id).filter(commentPo -> commentPo.getState()==1)
                .map(Comment::new).map(comment -> {
                    comment.setCustomer(nacosHelp.findUserById(comment.getCustomer().getId()));
                    return comment;
                }).collect(Collectors.toList());
        System.out.println(comments.block());
        return comments.map(commentList -> {
                    PageInfo<Comment> retPage=new PageInfo<>(commentList);
                    retPage.setPages(page);
                    retPage.setPageNum(page);
                    retPage.setPageSize(pageSize);
                    retPage.setTotal(pageSize);
                    return retPage;
                }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> auditComment(Long id, CommentVo commentVo) {
        return commentRepository.findById(id).defaultIfEmpty(new CommentPo())
                .flatMap(commentPo -> {
                    if(commentPo.getId()!=null){
                        commentPo.setState(commentVo.getState());
                        return commentRepository.save(commentPo);
                    }else {
                        return Mono.just(commentPo);
                    }
                }).map(Comment::new)
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> showComment(Long userId, Integer page, Integer pageSize) {
        return commentRepository.findAllByCustomerId(userId).map(Comment::new)
                .map(comment -> {
            comment.setCustomer(nacosHelp.findUserById(comment.getCustomer().getId()));
            return comment;
        }).collect(Collectors.toList()).map(comments -> {
                    PageInfo<Comment> retPage=new PageInfo<>(comments);
                    retPage.setPages(page);
                    retPage.setPageNum(page);
                    retPage.setPageSize(pageSize);
                    retPage.setTotal(pageSize);
                    return retPage;
                }).map(ReturnObject::new);
    }

    public Mono<ReturnObject> showUnAuditComments(Long userId, Long id, Integer state,Integer page,Integer pageSize) {
        return commentRepository.findAll().filter(commentPo->commentPo.getState()==state)
                .map(Comment::new).map(comment -> {
                    comment.setCustomer(nacosHelp.findUserById(comment.getCustomer().getId()));
                    return comment;
                }).collect(Collectors.toList()).map(comments->{
                    PageInfo<Comment> retPage=new PageInfo<>(comments);
                    retPage.setPages(page);
                    retPage.setPageNum(page);
                    retPage.setPageSize(pageSize);
                    retPage.setTotal(pageSize);
                    return new ReturnObject(retPage);
                });
    }
}
