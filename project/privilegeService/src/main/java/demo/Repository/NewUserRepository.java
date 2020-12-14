package demo.Repository;

import demo.model.po.NewUserPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface NewUserRepository extends ReactiveCrudRepository<NewUserPo,Long> {
}
