package demo.Repository;

import demo.model.po.RolePrivilegePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RolePrivilegeRepository extends ReactiveCrudRepository<RolePrivilegePo,Long> {

    Flux<RolePrivilegePo> findAllByRoleId(Long roleId);

}
