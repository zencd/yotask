package svc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import svc.entity.SimCard;
import svc.entity.SimQuota;
import svc.entity.SimQuotaType;
import svc.repository.SimCardRepository;
import svc.repository.SimQuotaRepository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

import static svc.TestUtils.makeDate;

/**
 * Adds some test data to a demo instance.
 */
@Configuration
public class DemoDataConfig {

    private SimCardRepository simCardRepository;
    private SimQuotaRepository simQuotaRepository;

    @Autowired
    public DemoDataConfig(SimCardRepository simCardRepository, SimQuotaRepository simQuotaRepository) {
        this.simCardRepository = simCardRepository;
        this.simQuotaRepository = simQuotaRepository;
    }

    @PostConstruct
    @Transactional
    public void postConstruct() {
        SimCard sim = new SimCard();
        sim.setMsisdn("79001234567");
        simCardRepository.save(sim);

        {
            SimQuota q = new SimQuota();
            q.setSimCard(sim);
            q.setType(SimQuotaType.VOICE);
            q.setBalance(new BigDecimal(60));
            q.setEndDate(makeDate(2021, 1, 1));
            simQuotaRepository.save(q);
        }

        {
            SimQuota q = new SimQuota();
            q.setSimCard(sim);
            q.setType(SimQuotaType.TRAFFIC);
            q.setBalance(new BigDecimal(2048));
            q.setEndDate(makeDate(2021, 1, 1));
            simQuotaRepository.save(q);
        }
    }

}
