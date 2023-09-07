package svc.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import svc.entity.SimCardEntity;
import svc.entity.SimQuotaEntity;
import svc.dto.SimQuotaType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface SimQuotaRepository extends CrudRepository<SimQuotaEntity, Long>  {

    @Query("SELECT SUM(q.balance) FROM SimQuotaEntity q where q.status = svc.entity.SimQuotaStatus.ENABLED and q.simCard = :sim and q.type = :type and q.endDate > :now")
    Optional<BigDecimal> sumQuota(@Param("sim") SimCardEntity sim, @Param("type") SimQuotaType type, @Param("now") OffsetDateTime now);

    @Query("SELECT q FROM SimQuotaEntity q where q.status = svc.entity.SimQuotaStatus.ENABLED and q.simCard = :sim and q.type = :type and q.endDate > :now")
    List<SimQuotaEntity> findAllActiveQuota(@Param("sim") SimCardEntity sim, @Param("type") SimQuotaType type, @Param("now") OffsetDateTime now);

    @Query("UPDATE SimQuotaEntity q set q.status = svc.entity.SimQuotaStatus.DISABLED where q.status = svc.entity.SimQuotaStatus.ENABLED and q.endDate < :now")
    @Modifying
    @Transactional
    int disableStaleQuotas(@Param("now") OffsetDateTime now);
}
