package svc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConsumeQuotaRequest {

    @NotNull
    @Schema(description = "Тип пакета: голос или интернет")
    SimQuotaType type;

    @NotNull
    @Positive
    @Schema(description = "Число минут или мегабайт, в зависимости от `type`")
    BigDecimal amount;

    @JsonIgnore
    long simId;
}
