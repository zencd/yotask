package svc.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SimQuotaStatus {

    DISABLED(0), ENABLED(1);

    private int intValue;

    SimQuotaStatus(int intValue) {
        if (ordinal() != intValue) {
            // XXX Using ordinal in DB, so make sure we won't lose correct mapping after enums reordered
            throw new IllegalArgumentException("The `intValue` must increment along with its ordinal");
        }
        this.intValue = intValue;
    }

    @JsonValue
    public int jsonValue() {
        return intValue;
    }
}
