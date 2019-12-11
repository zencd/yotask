package svc.repository;

import svc.entity.SimCard;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SimCardRepository extends CrudRepository<SimCard, Long> {
    List<SimCard> findAll();
}
