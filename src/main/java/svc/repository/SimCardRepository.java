package svc.repository;

import svc.entity.SimCardEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SimCardRepository extends CrudRepository<SimCardEntity, Long> {
    List<SimCardEntity> findAll();
}
