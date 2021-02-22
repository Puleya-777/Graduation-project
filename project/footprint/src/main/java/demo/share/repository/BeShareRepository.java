package demo.share.repository;

import demo.share.model.po.BeSharePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface BeShareRepository extends ReactiveCrudRepository<BeSharePo,Long> {
}
