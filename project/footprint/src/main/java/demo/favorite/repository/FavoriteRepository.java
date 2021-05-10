package demo.favorite.repository;

import demo.favorite.model.po.FavoritePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
public interface FavoriteRepository extends ReactiveCrudRepository<FavoritePo,Long> {
    Flux<FavoritePo> findAllByCustomerId(Long customerId);
    Mono<Integer> deleteFavoritePoById(Long id);
}
