package demo.repository;

import demo.model.po.UserRolePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRoleRepository extends ReactiveCrudRepository<UserRolePo,Long> {

    Mono<Integer> deleteUserRolePoByUserIdAndAndRoleId(Long userId,Long roleId);

    Mono<UserRolePo> findByUserIdAndRoleId(Long userId,Long roleId);

    Flux<UserRolePo> findAllByUserId(Long userId);

    Flux<UserRolePo> findByRoleId(Long roleId);
}
