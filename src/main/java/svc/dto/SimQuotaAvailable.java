package svc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Info about quota available to a SIM card")
public class SimQuotaAvailable {

    @Schema(description = "Число доступных минут")
    BigDecimal minutes;

    @Schema(description = "Доступный интернет-трафик, МБ")
    BigDecimal megabytes;
}
