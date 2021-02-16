package com.example.service;

import com.example.model.po.CommentPo;
import com.example.model.vo.CommentVo;
import com.example.repository.CommentRepository;
import com.example.util.ReturnObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Resource
    CommentRepository commentRepository;


    public Mono<ReturnObject> addSkuComment(Long orderItemId, CommentVo commentVo) {
        CommentPo commentPo=new CommentPo(commentVo);
        return commentRepository.save(commentPo).map(ReturnObject::new);
    }

    public Mono<ReturnObject> getSkuComment(Long id, Integer page, Integer pageSize) {
        return commentRepository.findAllByGoodsSkuId(id).filter(commentPo -> commentPo.getState()==1)
                .collect(Collectors.toList()).map(ReturnObject::new);
    }

    public Mono<ReturnObject> auditComment(Long id, CommentVo commentVo) {
        return commentRepository.findById(id)
                .map(commentPo -> {
                    commentPo.setState(commentVo.getState());
                    return commentPo;
                })
                .map(ReturnObject::new);
    }

    public Mono<ReturnObject> showComment(Long userId, Long page, Long pageSize) {
        return commentRepository.findAllByCustomerId(userId)
                .collect(Collectors.toList()).map(ReturnObject::new);
    }

    public Mono<ReturnObject> showUnAuditComments(Long userId, Long id, Integer state) {
        return commentRepository.findAll().filter(commentPo->commentPo.getState()==state)
                .collect(Collectors.toList()).map(ReturnObject::new);
    }
}
