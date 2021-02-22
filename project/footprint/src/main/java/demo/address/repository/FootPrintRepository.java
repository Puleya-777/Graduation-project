package demo.address.repository;

import demo.address.model.po.FootPrintPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface FootPrintRepository extends ReactiveCrudRepository<FootPrintPo,Long> {
}
