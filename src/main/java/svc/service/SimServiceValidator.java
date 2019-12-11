package svc.service;

import org.springframework.stereotype.Component;
import svc.dto.ConsumeQuotaRequest;
import svc.dto.CreateQuotaRequest;
import svc.entity.SimQuotaType;
import svc.exceptions.IncorrectRequestException;

/**
 * Validates data and retrieves required things (maybe).
 */
@Component
public class SimServiceValidator {

    public void validate(long simId, CreateQuotaRequest request) {
        request.simId = simId;
        SimQuotaType type = SimQuotaType.fromCode(request.type);
        if (type == null) {
            throw new IncorrectRequestException("Param `type` not recognized");
        }
        request.typeObj = type;
    }

    public void validate(long simId, ConsumeQuotaRequest request) {
        request.simId = simId;
        SimQuotaType type = SimQuotaType.fromCode(request.type);
        if (type == null) {
            throw new IncorrectRequestException("Param `type` not recognized");
        }
        request.typeObj = type;
    }

}
