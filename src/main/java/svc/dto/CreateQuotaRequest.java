package svc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request model for method \"create quota\"")
public class CreateQuotaRequest {

    @NotNull
    @Schema(description = "Тип пакета: голос или интернет", allowableValues = "voice, traffic")
    SimQuotaType type;

    @NotNull
    @Positive
    @Schema(description = "Число минут или мегабайт, в зависимости от `type`")
    BigDecimal amount;

    @NotNull
    @Schema(description = "Дата окончания срока действия пакета", example = "2019-12-11T03:56:16+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime endDate;

    @JsonIgnore
    long simId;
}
