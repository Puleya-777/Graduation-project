package demo.advertise.repository;

import demo.advertise.model.po.TimeSegmentPo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author chei1
 */
public interface TimeSegmentRepository extends ReactiveCrudRepository<TimeSegmentPo,Long> {
}
