package svc.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SimCardStatus {
    DISABLED(0), ENABLED(1);

    private int intValue;
    SimCardStatus(int intValue) {
        if (ordinal() != intValue) {
            // XXX Using EnumType.ORDINAL in entities, so make sure the enum values are not reordered randomly.
            throw new IllegalArgumentException("The `intValue` must increment along with its ordinal");
        }
        this.intValue = intValue;
    }

    @JsonValue
    public int toValue() {
        return intValue;
    }

    public static SimCardStatus fromBoolean(boolean enabled) {
        return enabled ? SimCardStatus.ENABLED : SimCardStatus.DISABLED;
    }
}
