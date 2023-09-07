package svc.service;

import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuota;
import svc.dto.SimQuotaAvailable;

public interface QuotaService {

    SimQuotaAvailable getQuotaAvailable(long simId);

    SimQuota createQuota(CreateQuotaRequest request);

    void consumeQuota(ConsumeQuotaRequest request);

    void disableStaleQuotas();
}
