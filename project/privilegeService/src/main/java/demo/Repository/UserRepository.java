package demo.Repository;

import demo.model.po.UserPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<UserPo,Long> {



}
