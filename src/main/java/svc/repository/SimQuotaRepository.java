package svc.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import svc.entity.SimCard;
import svc.entity.SimQuota;
import svc.entity.SimQuotaType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface SimQuotaRepository extends CrudRepository<SimQuota, Long>  {
    @Query("SELECT SUM(q.balance) FROM SimQuota q where q.status = svc.entity.SimQuotaStatus.ENABLED and q.simCard = :sim and q.type = :type and q.endDate > :now")
    BigDecimal sumQuota(@Param("sim") SimCard sim, @Param("type") SimQuotaType type, @Param("now") Date now);

    @Query("SELECT q FROM SimQuota q where q.status = svc.entity.SimQuotaStatus.ENABLED and q.simCard = :sim and q.type = :type and q.endDate > :now")
    List<SimQuota> findAllActiveQuota(@Param("sim") SimCard sim, @Param("type") SimQuotaType type, @Param("now") Date now);

    @Query("UPDATE SimQuota q set q.status = svc.entity.SimQuotaStatus.DISABLED where q.status = svc.entity.SimQuotaStatus.ENABLED and q.endDate < :now")
    @Modifying
    @Transactional
    int disableStaleQuotas(@Param("now") Date now);
}
