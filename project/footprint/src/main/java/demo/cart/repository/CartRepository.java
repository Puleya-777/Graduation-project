package demo.cart.repository;

import demo.cart.model.po.CartPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface CartRepository extends ReactiveCrudRepository<CartPo,Long> {
}
