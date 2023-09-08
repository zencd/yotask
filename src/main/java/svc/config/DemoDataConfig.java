package svc.config;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import svc.entity.SimCardEntity;
import svc.entity.SimQuotaEntity;
import svc.entity.SimQuotaType;
import svc.repository.SimCardRepository;
import svc.repository.SimQuotaRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Adds some test data to the demo DB.
 */
@Profile("demo")
@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DemoDataConfig {

    SimCardRepository simCardRepository;
    SimQuotaRepository simQuotaRepository;

    @PostConstruct
    @Transactional
    public void postConstruct() {
        SimCardEntity sim = SimCardEntity.builder().msisdn("79001234567").build();
        simCardRepository.save(sim);

        simQuotaRepository.save(SimQuotaEntity.builder()
                .simCard(sim)
                .type(SimQuotaType.VOICE)
                .balance(new BigDecimal(60))
                .endDate(OffsetDateTime.of(2099, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .build());

        simQuotaRepository.save(SimQuotaEntity.builder()
                .simCard(sim)
                .type(SimQuotaType.TRAFFIC)
                .balance(new BigDecimal(2048))
                .endDate(OffsetDateTime.of(2099, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .build());
    }

}
