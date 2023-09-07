package svc.entity;

import org.springframework.util.Assert;

/**
 * Status of a single {@link SimQuotaEntity}.
 * Once the quota is exhausted or outdated, it gonna become DISABLED.
 */
public enum SimQuotaStatus {

    DISABLED(0), ENABLED(1);

    SimQuotaStatus(int intValue) {
        Assert.isTrue(ordinal() == intValue, "intValue must follow ordinal to keep DB mapping safe");
    }
}
