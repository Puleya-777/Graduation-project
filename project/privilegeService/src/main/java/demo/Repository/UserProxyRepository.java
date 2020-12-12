package demo.Repository;

import demo.model.po.UserProxyPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface UserProxyRepository extends ReactiveCrudRepository<UserProxyPo,Long> {
    Flux<UserProxyPo> findAllByUserBId(Long id);
}
