package demo.repository;

import demo.model.po.PrivilegePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface PrivilegeRepository extends ReactiveCrudRepository<PrivilegePo,Long> {

    Mono<PrivilegePo> findByUrlAndRequestType(String url,Byte requestType);

}
