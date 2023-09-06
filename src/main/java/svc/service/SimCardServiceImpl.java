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
import svc.dto.SimQuotaInfo;
import svc.entity.SimCard;
import svc.entity.SimCardStatus;
import svc.entity.SimQuota;
import svc.dto.SimQuotaType;
import svc.exception.NotFoundException;
import svc.mapper.SimQuotaMapper;
import svc.repository.SimCardRepository;
import svc.repository.SimQuotaRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * A service managing sim cards.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SimCardServiceImpl implements SimCardService {

    SimCardRepository simCardRepository;
    SimQuotaRepository simQuotaRepository;
    SimQuotaMapper simQuotaMapper;

    public void activateSim(long simId, boolean enabled) {
        SimCard sim = simCardRepository.findById(simId).orElseThrow(NotFoundException::new);
        sim.setStatus(SimCardStatus.fromBoolean(enabled));
        simCardRepository.save(sim);
    }

    @Override
    public SimQuotaInfo getQuotaAvailable(long simId) {
        SimCard sim = simCardRepository.findById(simId).orElseThrow(NotFoundException::new);
        var now = OffsetDateTime.now();
        BigDecimal voiceBalance = simQuotaRepository.sumQuota(sim, SimQuotaType.VOICE, now).orElse(BigDecimal.ZERO);
        BigDecimal trafficBalance = simQuotaRepository.sumQuota(sim, SimQuotaType.TRAFFIC, now).orElse(BigDecimal.ZERO);
        return SimQuotaInfo.builder()
                .megabytes(trafficBalance)
                .minutes(voiceBalance)
                .build();
    }

    @Override
    public SimQuota createQuota(CreateQuotaRequest request) {
        SimCard sim = simCardRepository.findById(request.simId).orElseThrow(NotFoundException::new);
        SimQuota quota = simQuotaMapper.toSimQuota(request, sim);
        simQuotaRepository.save(quota);
        return quota;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void consumeQuota(ConsumeQuotaRequest request) {
        SimCard sim = simCardRepository.findById(request.simId).orElseThrow(NotFoundException::new);

        BigDecimal toChargeYet = request.amount;

        // XXX no algorithm given how to charge minutes/megabytes so do it in random order
        // order by creation date maybe
        List<SimQuota> quotas = simQuotaRepository.findAllActiveQuota(sim, request.type, OffsetDateTime.now());
        for (SimQuota aQuota : quotas) {
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
