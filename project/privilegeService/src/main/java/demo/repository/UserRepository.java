package demo.repository;

import demo.model.po.UserPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserPo,Long> {

    Flux<UserPo> findAllByUserNameAndMobile(String userName,String mobile);

    Flux<UserPo> findAllByDepartId(Long departId);

    Mono<UserPo> findByMobile(String mobile);

    Mono<UserPo> findByEmail(String email);

    Mono<UserPo> findByUserName(String username);

    Mono<UserPo> findByUserNameAndMobile(String userName,String mobile);
}
