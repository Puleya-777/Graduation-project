package demo.repository;

import demo.model.po.RolePrivilegePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RolePrivilegeRepository extends ReactiveCrudRepository<RolePrivilegePo,Long> {

    Flux<RolePrivilegePo> findAllByRoleId(Long roleId);

    Mono<RolePrivilegePo> findByRoleIdAndPrivilegeId(Long id, Long pid);

}
