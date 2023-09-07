package svc.dto;

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
public class SimQuota {

    Long id;
    SimQuotaStatus status;
    SimQuotaType type;
    BigDecimal balance;
    OffsetDateTime endDate;
    OffsetDateTime dateCreated;
    OffsetDateTime lastUpdated;

}
