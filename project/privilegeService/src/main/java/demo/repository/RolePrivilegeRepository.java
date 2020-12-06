package demo.repository;

import demo.model.po.RolePrivilegePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * @author chei1
 */
public interface RolePrivilegeRepository extends ReactiveCrudRepository<RolePrivilegePo,Long> {
    Flux<RolePrivilegePo> findAllByPrivilegeIdAndRoleId(Long privilegeId,Long roleId);
}
