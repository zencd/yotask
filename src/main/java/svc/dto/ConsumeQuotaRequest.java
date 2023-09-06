package svc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Request model for method \"consume quota\"")
public class ConsumeQuotaRequest {

    @Schema(description = "Тип пакета: голос или интернет")
    @NotNull
    public SimQuotaType type;

    @NotNull
    @Positive
    @Schema(description = "Число минут или мегабайт, в зависимости от `type`")
    public BigDecimal amount;

    @JsonIgnore
    public long simId;
}
