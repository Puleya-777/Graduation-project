package demo.Repository;

import demo.model.po.PrivilegePo;
import demo.model.po.UserPo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface PrivilegeRepository extends ReactiveCrudRepository<PrivilegePo,Long> {

    Mono<PrivilegePo> findByUrlAndRequestType(String url,Byte requestType);

}
