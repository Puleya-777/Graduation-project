package demo.share.repository;

import demo.share.model.po.BeSharePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface BeShareRepository extends ReactiveCrudRepository<BeSharePo,Long> {
    Flux<BeSharePo> findAllBySharerId(Long id);
    Flux<BeSharePo> findAllByGoodsSkuId(Long id);
}
