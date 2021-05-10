package com.order.dao;

import com.order.model.bo.Shop;
import com.order.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Repository
public class ShopDao {
    @Autowired
    ShopRepository shopRepository;
    public Mono<Shop> select(@NotNull Long id) {
        return shopRepository.findById(id).map(res->new Shop(res));
    }
}
