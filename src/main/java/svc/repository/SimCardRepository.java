package svc.repository;

import org.springframework.data.repository.CrudRepository;
import svc.entity.SimCardEntity;

public interface SimCardRepository extends CrudRepository<SimCardEntity, Long> {
}
