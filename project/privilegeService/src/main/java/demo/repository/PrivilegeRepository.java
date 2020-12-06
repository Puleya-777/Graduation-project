package demo.repository;

import demo.model.po.PrivilegePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface PrivilegeRepository extends ReactiveCrudRepository<PrivilegePo,Long> {
}
