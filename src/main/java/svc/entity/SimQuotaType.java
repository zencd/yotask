package svc.entity;

import org.springframework.util.Assert;

public enum SimQuotaType {

    VOICE(0), TRAFFIC(1);

    SimQuotaType(int intValue) {
        Assert.isTrue(ordinal() == intValue, "intValue must follow ordinal to keep DB mapping safe");
    }
}
