package demo.repository;

import demo.model.po.UserPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface UserRepository extends ReactiveCrudRepository<UserPo,Long> {
}
