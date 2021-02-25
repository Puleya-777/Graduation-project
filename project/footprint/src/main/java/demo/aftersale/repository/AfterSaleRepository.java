package demo.aftersale.repository;

import demo.aftersale.model.po.AfterSalePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface AfterSaleRepository extends ReactiveCrudRepository<AfterSalePo,Long> {
    Flux<AfterSalePo> findAllByCustomerIdAndBeDeleted(Long customerId,Boolean flag);
    Flux<AfterSalePo> findAllByBeDeleted(Boolean flag);
    Mono<AfterSalePo> findByIdAndBeDeleted(Long id,Boolean flag);
}
