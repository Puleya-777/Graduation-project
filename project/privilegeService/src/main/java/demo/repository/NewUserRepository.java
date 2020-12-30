package demo.repository;

import demo.model.po.NewUserPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NewUserRepository extends ReactiveCrudRepository<NewUserPo,Long> {
    Flux<NewUserPo> findAllByDepartId(Long did);
    Mono<Integer> deleteNewUserPoById(Long id);
    Mono<NewUserPo> findByUserName(String username);
    Mono<NewUserPo> findByMobile(String mobile);
    Mono<NewUserPo> findByEmail(String email);
    Mono<Integer> countAllByUserName(String username);
    Mono<Integer> countAllByMobile(String mobile);
    Mono<Integer> countAllByEmail(String email);
}
