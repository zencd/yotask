package svc.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuota;
import svc.dto.SimQuotaAvailable;
import svc.entity.SimCardEntity;
import svc.entity.SimQuotaEntity;
import svc.entity.SimQuotaType;
import svc.exception.NotFoundException;
import svc.mapper.SimQuotaMapper;
import svc.repository.SimCardRepository;
import svc.repository.SimQuotaRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * A service managing sim card quota.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class QuotaServiceImpl implements QuotaService {

    SimCardRepository simCardRepository;
    SimQuotaRepository simQuotaRepository;
    SimQuotaMapper simQuotaMapper;

    @Override
    public SimQuotaAvailable getQuotaAvailable(long simId) {
        SimCardEntity sim = simCardRepository.findById(simId).orElseThrow(NotFoundException::new);
        var now = OffsetDateTime.now();
        BigDecimal voiceBalance = simQuotaRepository.sumQuota(sim, SimQuotaType.VOICE, now).orElse(BigDecimal.ZERO);
        BigDecimal trafficBalance = simQuotaRepository.sumQuota(sim, SimQuotaType.TRAFFIC, now).orElse(BigDecimal.ZERO);
        return SimQuotaAvailable.builder()
                .traffic(trafficBalance)
                .voice(voiceBalance)
                .build();
    }

    @Override
    public SimQuota createQuota(CreateQuotaRequest request) {
        SimCardEntity sim = simCardRepository.findById(request.getSimId()).orElseThrow(NotFoundException::new);
        SimQuotaEntity quota = simQuotaMapper.toSimQuota(request, sim);
        simQuotaRepository.save(quota);
        return simQuotaMapper.toSimQuotaDto(quota);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void consumeQuota(ConsumeQuotaRequest request) {
        SimCardEntity sim = simCardRepository.findById(request.getSimId()).orElseThrow(NotFoundException::new);

        BigDecimal toChargeYet = request.getAmount();

        svc.entity.SimQuotaType simQuotaType = simQuotaMapper.toEntity(request.getType());
        List<SimQuotaEntity> quotas = simQuotaRepository.findAllActiveQuota(sim, simQuotaType, OffsetDateTime.now());
        for (SimQuotaEntity aQuota : quotas) {
            if (toChargeYet.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal aCharge = toChargeYet.min(aQuota.getBalance());
            if (aCharge.compareTo(BigDecimal.ZERO) > 0) {
                toChargeYet = toChargeYet.subtract(aCharge);
                BigDecimal newBalance = aQuota.getBalance().subtract(aCharge);
                aQuota.setBalance(newBalance);
                simQuotaRepository.save(aQuota);
            }
        }
    }

    @Override
    public void disableStaleQuotas() {
        log.info("disableStaleQuotas...");
        simQuotaRepository.disableStaleQuotas(OffsetDateTime.now());
    }
}
