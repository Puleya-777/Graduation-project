package com.example.repository;


import com.example.model.po.CommentPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends ReactiveCrudRepository<CommentPo,Long> {

    Flux<CommentPo> findAllByGoodsSkuId(Long goodsSkuId);

    Flux<CommentPo> findAllByCustomerId(Long customerId);
}
