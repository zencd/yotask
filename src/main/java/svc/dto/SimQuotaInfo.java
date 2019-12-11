package svc.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Info about quota available to a SIM card.
 */
public class SimQuotaInfo {
    /**
     * Amount of minutes available.
     */
    @ApiModelProperty(value = "Число минут")
    public BigDecimal minutes;

    /**
     * Amount of internet traffic available.
     * GB looks not too precise enough.
     * MB looks just fine now.
     */
    @ApiModelProperty(value = "Количество интернет-трафика, МБ")
    public BigDecimal megabytes;

    public SimQuotaInfo() {
    }

    public SimQuotaInfo(BigDecimal minutes, BigDecimal megabytes) {
        this.minutes = minutes;
        this.megabytes = megabytes;
    }

    public BigDecimal getMinutes() {
        return minutes;
    }

    public void setMinutes(BigDecimal minutes) {
        this.minutes = minutes;
    }

    public BigDecimal getMegabytes() {
        return megabytes;
    }

    public void setMegabytes(BigDecimal megabytes) {
        this.megabytes = megabytes;
    }
}
