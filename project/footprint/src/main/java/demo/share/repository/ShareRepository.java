package demo.share.repository;

import demo.share.model.po.SharePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface ShareRepository extends ReactiveCrudRepository<SharePo,Long> {
}
