package svc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Info about quota available to a SIM card")
public class SimQuotaInfo {

    @Schema(description = "Число доступных минут")
    public BigDecimal minutes;

    /*
     * Amount of internet traffic available.
     * GB looks not too precise enough.
     * MB looks just fine now.
     */
    @Schema(description = "Количество доступного интернет-трафика, МБ")
    public BigDecimal megabytes;
}
