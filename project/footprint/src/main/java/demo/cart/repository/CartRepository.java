package demo.cart.repository;

import demo.cart.model.po.CartPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface CartRepository extends ReactiveCrudRepository<CartPo,Long> {
    Flux<CartPo> findAllByCustomerId(Long customerId);
    Mono<Integer> deleteCartPoById(Long id);
    Mono<Integer> deleteCartPosByCustomerId(Long customerId);
    Mono<CartPo> findAllByCustomerIdAndGoodsSkuId(Long customerId,Long goodSkuId);
    Mono<Integer> countAllByCustomerIdAndGoodsSkuId(Long customerId,Long goodSkuId);
}
