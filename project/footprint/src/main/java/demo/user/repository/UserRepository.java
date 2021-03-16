package demo.user.repository;

import demo.user.model.po.UserPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface UserRepository extends ReactiveCrudRepository<UserPo,Long> {
    Mono<Integer> countAllByEmail(String email);
    Mono<Integer> countAllByMobile(String mobile);
    Mono<Integer> countAllByUserName(String username);
    Mono<UserPo> findByUserName(String userName);
    Mono<UserPo> findByEmail(String email);
    Mono<UserPo> findByMobile(String mobile);
}
