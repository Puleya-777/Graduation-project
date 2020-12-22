package demo.Repository;

import demo.model.po.RolePo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface RoleRepository extends ReactiveCrudRepository<RolePo,Long> {

    Flux<RolePo> findAllByDepartId(Long departId);


}
