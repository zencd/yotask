package svc.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status of a single {@link SimQuota}.
 * Once the quota is exhausted or outdated, it gonna become DISABLED.
 */
public enum SimQuotaStatus {

    DISABLED(0), ENABLED(1);

    private int intValue;

    SimQuotaStatus(int intValue) {
        if (ordinal() != intValue) {
            // XXX Using EnumType.ORDINAL in entities, so make sure the enum values are not reordered randomly.
            throw new IllegalArgumentException("The `intValue` must increment along with its ordinal");
        }
        this.intValue = intValue;
    }

    @JsonValue
    public int jsonValue() {
        return intValue;
    }

    public static SimCardStatus fromBoolean(boolean enabled) {
        return enabled ? SimCardStatus.ENABLED : SimCardStatus.DISABLED;
    }
}
