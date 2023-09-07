package svc.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum SimQuotaType {

    VOICE(0, "voice"), TRAFFIC(1, "traffic");

    private int intValue;
    private String code;

    private static class Helper {
        static final Map<String, SimQuotaType> BY_CODE = new HashMap<>();
    }

    SimQuotaType(int intValue, String code) {
        if (ordinal() != intValue) {
            // XXX Using ordinal in DB, so make sure we won't lose correct mapping after enums reordered
            throw new IllegalArgumentException("The `intValue` must increment along with its ordinal");
        }
        this.intValue = intValue;
        this.code = code;
        Helper.BY_CODE.put(code, this);
    }

    @JsonValue
    public String jsonValue() {
        return code;
    }

    @JsonCreator
    public static SimQuotaType fromCode(String code) {
        return Helper.BY_CODE.get(code);
    }

}
