package demo.address.repository;

import demo.address.model.po.AddressPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface AddressRepository extends ReactiveCrudRepository<AddressPo,Long> {
    Flux<AddressPo> findAllByCustomerId(Long id);
    Mono<Integer> countAllByCustomerId(Long id);
    Mono<Integer> countAllByCustomerIdAndIsDefault(Long id,Boolean isDefault);
    Mono<AddressPo> findByCustomerIdAndIsDefault(Long id,Boolean isDefault);
    Mono<Integer> deleteAddressPoById(Long id);
}
