package svc.entity;

import org.springframework.util.Assert;

public enum SimCardStatus {

    DISABLED(0), ENABLED(1);

    SimCardStatus(int intValue) {
        Assert.isTrue(ordinal() == intValue, "intValue must follow ordinal to keep DB mapping safe");
    }

    public static SimCardStatus of(boolean enabled) {
        return enabled ? SimCardStatus.ENABLED : SimCardStatus.DISABLED;
    }
}
