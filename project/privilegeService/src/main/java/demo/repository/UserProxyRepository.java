package demo.repository;

import demo.model.po.UserProxyPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * @author chei1
 */
public interface UserProxyRepository extends ReactiveCrudRepository<UserProxyPo,Long> {
    Flux<UserProxyPo> findAllByUserBId(Long userBId);
}
