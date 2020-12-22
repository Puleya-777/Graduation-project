package demo.Repository;

import demo.model.bo.User;
import demo.model.po.UserPo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserPo,Long> {

    Flux<UserPo> findAllByUserNameAndMobile(String userName,String mobile);

    Flux<UserPo> findAllByDepartId(Long departId);

    Mono<UserPo> findByMobile(String mobile);

    Mono<UserPo> findByEmail(String email);

    Mono<UserPo> findByUserName(String userName);

    Mono<UserPo> findByUserNameAndMobile(String userName,String mobile);

}
