package demo.repository;

import demo.model.po.UserRolePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * @author chei1
 */
public interface UserRoleRepository extends ReactiveCrudRepository<UserRolePo,Long> {
    Flux<UserRolePo> findAllByRoleId(Long roleId);
}
