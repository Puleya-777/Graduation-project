package demo.repository;

import demo.model.bo.UserProxy;
import demo.model.po.UserProxyPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserProxyRepository extends ReactiveCrudRepository<UserProxyPo,Long> {
    Flux<UserProxyPo> findAllByUserBId(Long id);
    Mono<Integer> deleteUserProxyPoById(Long id);
    Flux<UserProxyPo> findAllByUserAId(Long id);

    Flux<UserProxyPo> findAllByUserAIdAndUserBId(Long aId,Long bid);
    Flux<UserProxyPo> findAllByDepartId(Long id);
}
