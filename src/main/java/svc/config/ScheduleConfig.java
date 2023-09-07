package svc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import svc.service.QuotaService;

/**
 * Scheduled tasks.
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {

    @Autowired
    QuotaService quotaService;

    @Scheduled(fixedDelay = 60_000, initialDelay = 60_000) // millis
    public void disableStaleQuotas() {
        quotaService.disableStaleQuotas();
    }
}
