package svc.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum SimQuotaType {

    VOICE("voice"), TRAFFIC("traffic");

    @Getter
    @JsonValue
    private final String code;

    private static class Helper {
        static final Map<String, SimQuotaType> BY_CODE = new HashMap<>();
    }

    SimQuotaType(String code) {
        this.code = code;
        Helper.BY_CODE.put(code, this);
    }

    @JsonCreator
    public static SimQuotaType fromCode(String code) {
        return Helper.BY_CODE.get(code);
    }

}
