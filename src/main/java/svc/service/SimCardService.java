package svc.service;

import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuotaInfo;
import svc.entity.SimCard;
import svc.entity.SimQuota;

import java.util.List;

public interface SimCardService {

    void activateSim(long simId, boolean enabled);

    SimQuotaInfo getQuotaAvailable(long simId);

    SimQuota createQuota(CreateQuotaRequest request);

    void consumeQuota(ConsumeQuotaRequest request);

    void disableStaleQuotas();
}
