package svc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "A REST model mirroring a quote from DB")
public class SimQuota {

    Long id;
    SimQuotaStatus status;
    SimQuotaType type;
    BigDecimal balance;
    OffsetDateTime endDate;
    OffsetDateTime dateCreated;
    OffsetDateTime lastUpdated;

}
