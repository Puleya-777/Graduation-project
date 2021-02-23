package demo.aftersale.repository;

import demo.aftersale.model.po.AfterSalePo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface AfterSaleRepository extends ReactiveCrudRepository<AfterSalePo,Long> {
}
