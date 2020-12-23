package demo.repository;

import demo.model.po.RolePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RoleRepository extends ReactiveCrudRepository<RolePo,Long> {

    Flux<RolePo> findAllByDepartId(Long departId);


}
