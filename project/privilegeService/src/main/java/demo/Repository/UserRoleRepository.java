package demo.Repository;

import demo.model.bo.UserRole;
import demo.model.po.UserPo;
import demo.model.po.UserRolePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRoleRepository extends ReactiveCrudRepository<UserRolePo,Long> {

    Mono<Integer> deleteUserRolePoByUserIdAndAndRoleId(Long userId,Long roleId);

    Mono<UserRolePo> findByUserIdAndRoleId(Long userId,Long roleId);

    Flux<UserRolePo> findAllByUserId(Long userId);
}
