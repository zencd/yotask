package svc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuotaInfo;
import svc.entity.SimCard;
import svc.entity.SimCardStatus;
import svc.entity.SimQuota;
import svc.entity.SimQuotaType;
import svc.exceptions.NotFoundException;
import svc.repository.SimCardRepository;
import svc.repository.SimQuotaRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * A service managing sim cards.
 */
@Service
public class SimCardServiceImpl implements SimCardService {

    private static final Logger log = LoggerFactory.getLogger(SimCardServiceImpl.class);

    private SimCardRepository simCardRepository;

    private SimQuotaRepository simQuotaRepository;

    @Autowired
    public SimCardServiceImpl(SimCardRepository simCardRepository, SimQuotaRepository simQuotaRepository) {
        this.simCardRepository = simCardRepository;
        this.simQuotaRepository = simQuotaRepository;
    }

    public void activateSim(long simId, boolean enabled) {
        SimCard sim = simCardRepository.findById(simId).orElseThrow(NotFoundException::new);
        sim.setStatus(SimCardStatus.fromBoolean(enabled));
        simCardRepository.save(sim);
    }

    /**
     * For development only.
     */
    @Override
    public List<SimCard> getAllSims() {
        return simCardRepository.findAll();
    }

    @Override
    public SimQuotaInfo getQuotaAvailable(long simId) {
        SimCard sim = simCardRepository.findById(simId).orElseThrow(NotFoundException::new);

        Date now = new Date();

        BigDecimal voiceBalance = simQuotaRepository.sumQuota(sim, SimQuotaType.VOICE, now);
        if (voiceBalance == null) {
            voiceBalance = BigDecimal.ZERO;
        }

        BigDecimal trafficBalance = simQuotaRepository.sumQuota(sim, SimQuotaType.TRAFFIC, now);
        if (trafficBalance == null) {
            trafficBalance = BigDecimal.ZERO;
        }

        SimQuotaInfo info = new SimQuotaInfo();
        info.setMegabytes(trafficBalance);
        info.setMinutes(voiceBalance);
        return info;
    }

    @Override
    public SimQuota createQuota(CreateQuotaRequest request) {
        SimCard sim = simCardRepository.findById(request.simId).orElseThrow(NotFoundException::new);

        SimQuota quota = new SimQuota();
        quota.setSimCard(sim);
        quota.setType(request.typeObj);
        quota.setBalance(request.amount);
        quota.setEndDate(request.endDate);

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
        List<SimQuota> quotas = simQuotaRepository.findAllActiveQuota(sim, request.typeObj, new Date());
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
        simQuotaRepository.disableStaleQuotas(new Date());
    }
}
