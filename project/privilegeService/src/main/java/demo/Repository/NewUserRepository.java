package demo.Repository;

import demo.model.po.NewUserPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NewUserRepository extends ReactiveCrudRepository<NewUserPo,Long> {
    Flux<NewUserPo> findAllByDepartId(Long did);
    Mono<Integer> deleteNewUserPoById(Long id);
}
