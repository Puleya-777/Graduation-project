package demo.share.repository;

import demo.share.model.po.ShareActivityPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface ShareActivityRepository extends ReactiveCrudRepository<ShareActivityPo,Long> {
    Flux<ShareActivityPo> findAllByShopIdAndGoodsSkuId(Long shopId, Long skuId);
    Flux<ShareActivityPo> findAllByShopId(Long shopId);
    Flux<ShareActivityPo> findAllByGoodsSkuId(Long skuId);
    Mono<ShareActivityPo> findByGoodsSkuId(Long skuId);
    Mono<Integer> deleteShareActivityPoById(Long id);
    Mono<Integer> countAllByGoodsSkuId(Long skuId);
}
