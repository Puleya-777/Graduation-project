package demo.Controller;

import demo.model.bo.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PrivilegeRepository extends ReactiveCrudRepository<User,Long> {
}
