package demo.Repository;

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
}
