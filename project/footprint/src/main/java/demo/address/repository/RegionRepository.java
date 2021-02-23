package demo.address.repository;

import demo.address.model.po.RegionPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface RegionRepository extends ReactiveCrudRepository<RegionPo,Long> {
    Flux<RegionPo> findAllByPid(Long pid);
    Mono<Integer> deleteRegionPoById(Long id);
}
