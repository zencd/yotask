package svc.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public enum SimQuotaType {

    VOICE(0, "voice"), TRAFFIC(1, "traffic");

    private int intValue;
    private String code;

    private static class Helper {
        static final Map<String, SimQuotaType> BY_CODE = new HashMap<>();
    }

    SimQuotaType(int intValue, String code) {
        if (ordinal() != intValue) {
            // XXX Using EnumType.ORDINAL in entities, so make sure the enum values are not reordered randomly.
            throw new IllegalArgumentException("The `intValue` must increment along with its ordinal");
        }
        this.intValue = intValue;
        this.code = code;
        Helper.BY_CODE.put(code, this);
    }

    @JsonValue
    public int toValue() {
        return intValue;
    }

    public static SimQuotaType fromCode(String code) {
        return Helper.BY_CODE.get(code);
    }

}
