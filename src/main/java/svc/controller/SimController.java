package svc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.dto.SimQuotaInfo;
import svc.entity.SimCard;
import svc.entity.SimQuota;
import svc.exception.IncorrectRequestException;
import svc.exception.NotFoundException;
import svc.service.SimCardService;
import svc.service.SimServiceValidator;

import javax.validation.Valid;
import java.util.List;

/**
 * Rest controller for managing sim cards and their quotas.
 */
@Api(description = "Операции с SIM-картами и пакетами минут/интернета")
@RestController
public class SimController {

    private static final Logger log = LoggerFactory.getLogger(SimController.class);

    private SimCardService simCardService;

    private SimServiceValidator validator;

    @Autowired
    public SimController(SimCardService simCardService, SimServiceValidator validator) {
        this.simCardService = simCardService;
        this. validator = validator;
    }

    /**
     * For development only.
     */
    @GetMapping("/sims")
    @ApiOperation(value = "For development only.")
    public List<SimCard> allSims() {
        return simCardService.getAllSims();
    }

    @PostMapping("/sims/{id}/quota/add")
    @ApiOperation(value = "Начислять пакеты минут и гигабайтов, имеющих время жизни")
    public SimQuota addQuota(
            @PathVariable("id") long simId,
            @RequestBody @Valid CreateQuotaRequest request) {
        log.info("addQuota() requested with simId: {}", simId);
        validator.validate(simId, request);
        return simCardService.createQuota(request);
    }

    @PostMapping("/sims/{id}/quota/consume")
    @ApiOperation(value = "Расходовать минуты и гигабайты")
    public void consumeQuota(
            @PathVariable("id") long simId,
            @RequestBody @Valid ConsumeQuotaRequest request) {
        log.info("consumeQuota() requested with simId: {}", simId);
        validator.validate(simId, request);
        simCardService.consumeQuota(request);
    }

    @GetMapping("/sims/{id}/quota/available")
    @ApiOperation(value = "Получать количество доступных минут и гигабайт")
    public SimQuotaInfo getQuotaAvailable(
            @PathVariable("id") long simId) {
        log.info("getQuotaAvailable() requested with simId: {}", simId);
        return simCardService.getQuotaAvailable(simId);
    }

    @GetMapping("/sims/{id}/activate")
    @ApiOperation(value = "Активировать и блокировать сим-карты")
    public void activateSim(
            @PathVariable("id") long simId,
            @RequestParam("enabled") boolean enabled) {
        log.info("activateSim() requested with simId: {}, enabled: {}", simId, enabled);
        simCardService.activateSim(simId, enabled);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> error404(NotFoundException e) {
        //return ResponseEntity.badRequest().body("Not found");
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IncorrectRequestException.class)
    public ResponseEntity<String> error400(IncorrectRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
