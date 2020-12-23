package demo.repository;

import demo.model.po.NewUserPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface NewUserRepository extends ReactiveCrudRepository<NewUserPo,Long> {
    Flux<NewUserPo> findAllByDepartId(Long did);
}
