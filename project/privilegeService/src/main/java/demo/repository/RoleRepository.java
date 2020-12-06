package demo.repository;

import demo.model.po.RolePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface RoleRepository extends ReactiveCrudRepository<RolePo,Long> {
}
