package svc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import svc.entity.SimQuotaType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Request model for method "create quota".
 */
@ApiModel
public class CreateQuotaRequest {

    @NotNull
    @ApiModelProperty(value = "Тип пакета: голос или интернет", allowableValues = "voice, traffic")
    @Pattern(regexp = "(voice|traffic)")
    public String type;

    @NotNull
    @Positive
    @ApiModelProperty(value = "Число минут или мегабайт, в зависимости от `type`")
    public BigDecimal amount;

    @NotNull
    @ApiModelProperty(value = "Дата окончания срока действия пакета", example = "2019-12-11T03:56:16+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date endDate;

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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}
