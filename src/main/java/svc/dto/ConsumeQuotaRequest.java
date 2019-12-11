package svc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import svc.entity.SimQuotaType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Request model for method "consume quota".
 */
@ApiModel
public class ConsumeQuotaRequest {

    @ApiModelProperty(value = "Тип пакета: голос или интернет", allowableValues = "voice, traffic")
    @NotNull
    @Pattern(regexp = "(voice|traffic)")
    public String type;

    @NotNull
    @Positive
    @ApiModelProperty(value = "Число минут или мегабайт, в зависимости от `type`")
    public BigDecimal amount;

    @JsonIgnore
    public long simId;

    @JsonIgnore
    public SimQuotaType typeObj;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
