package demo.advertise.repository;

import demo.advertise.model.po.AdvertisePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface AdvertiseRepository extends ReactiveCrudRepository<AdvertisePo,Long> {
    Mono<Integer> deleteAdvertisePoById(Long id);
    Mono<Integer> countAllBySegId(Long segId);
    Flux<AdvertisePo> findAllBySegId(Long segId);
}
