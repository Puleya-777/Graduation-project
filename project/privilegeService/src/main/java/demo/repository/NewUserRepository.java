package demo.repository;


import demo.model.po.NewUserPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface NewUserRepository extends ReactiveCrudRepository<NewUserPo,Long> {
}
