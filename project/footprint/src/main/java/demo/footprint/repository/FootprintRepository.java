package demo.footprint.repository;

import demo.footprint.model.po.FootprintPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * @author chei1
 */
public interface FootprintRepository extends ReactiveCrudRepository<FootprintPo,Long> {
    Flux<FootprintPo> findAllByCustomerId(Long customerId);
}
