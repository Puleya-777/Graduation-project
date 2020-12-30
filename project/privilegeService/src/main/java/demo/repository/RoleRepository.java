package demo.repository;

import demo.model.po.RolePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveCrudRepository<RolePo,Long> {

    Flux<RolePo> findAllByDepartId(Long departId);

    Mono<RolePo> findByName(String name);

    Mono<RolePo> findByIdAndDepartId(Long id,Long did);

    Mono<Integer> deleteByIdAndDepartId(Long id,Long did);

}
