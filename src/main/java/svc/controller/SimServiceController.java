package svc.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuota;
import svc.dto.SimQuotaAvailable;
import svc.service.QuotaService;
import svc.service.SimService;

/**
 * Rest controller for managing sim cards and their quotas.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SimServiceController {

    SimService simService;
    QuotaService quotaService;

    @PostMapping("/sims/{id}/quota/add")
    @Operation(summary = "Начислять пакеты минут и гигабайтов, имеющих время жизни")
    public SimQuota addQuota(
            @PathVariable("id") long simId,
            @RequestBody @Valid CreateQuotaRequest request) {
        log.info("addQuota() requested with simId: {}", simId);
        request.setSimId(simId);
        return quotaService.createQuota(request);
    }

    @PostMapping("/sims/{id}/quota/consume")
    @Operation(summary = "Расходовать минуты и гигабайты")
    public void consumeQuota(
            @PathVariable("id") long simId,
            @RequestBody @Valid ConsumeQuotaRequest request) {
        log.info("consumeQuota() requested with simId: {}", simId);
        request.setSimId(simId);
        quotaService.consumeQuota(request);
    }

    @GetMapping("/sims/{id}/quota/available")
    @Operation(summary = "Получать количество доступных минут и гигабайт")
    public SimQuotaAvailable getQuotaAvailable(
            @PathVariable("id") long simId) {
        log.info("getQuotaAvailable() requested with simId: {}", simId);
        return quotaService.getQuotaAvailable(simId);
    }

    @PostMapping("/sims/{id}/activate")
    @Operation(summary = "Активировать и блокировать сим-карты")
    public void activateSim(
            @PathVariable("id") long simId,
            @RequestParam("enabled") boolean enabled) {
        log.info("activateSim() requested with simId: {}, enabled: {}", simId, enabled);
        simService.activateSim(simId, enabled);
    }
}
