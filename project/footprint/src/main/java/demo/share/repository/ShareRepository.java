package demo.share.repository;

import demo.share.model.po.SharePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface ShareRepository extends ReactiveCrudRepository<SharePo,Long> {
    Mono<SharePo> findByShareUrl(String url);
    Flux<SharePo> findAllBySharerId(Long sharerId);
    Flux<SharePo> findAllByGoodsSkuId(Long goodsSku);
}
