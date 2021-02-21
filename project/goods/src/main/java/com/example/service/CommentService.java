package com.example.service;

import com.example.model.VoObject;
import com.example.model.bo.Comment;
import com.example.model.po.CommentPo;
import com.example.model.vo.CommentVo;
import com.example.repository.CommentRepository;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Resource
    CommentRepository commentRepository;


    public Mono<ReturnObject> addSkuComment(Long orderItemId, CommentVo commentVo) {
        CommentPo commentPo=new CommentPo(commentVo);
        return commentRepository.save(commentPo).map(Comment::new).map(ReturnObject::new);
    }

    public Mono<ReturnObject> getSkuComment(Long id, Integer page, Integer pageSize) {
        Mono<List<Comment>> comments=commentRepository.findAllByGoodsSkuId(id).filter(commentPo -> commentPo.getState()==1)
                .map(Comment::new).collect(Collectors.toList());
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
                .collect(Collectors.toList()).map(comments -> {
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
                .map(Comment::new).collect(Collectors.toList()).map(comments->{
                    PageInfo<Comment> retPage=new PageInfo<>(comments);
                    retPage.setPages(page);
                    retPage.setPageNum(page);
                    retPage.setPageSize(pageSize);
                    retPage.setTotal(pageSize);
                    return new ReturnObject(retPage);
                });
    }
}
