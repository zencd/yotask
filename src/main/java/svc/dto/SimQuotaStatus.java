package svc.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum SimQuotaStatus {

    DISABLED("disabled"), ENABLED("enabled");

    @Getter
    @JsonValue
    private final String code;

    SimQuotaStatus(String code) {
        this.code = code;
    }
}
