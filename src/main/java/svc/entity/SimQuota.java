package svc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * A single quota (packet) available to a SIM.
 * There can be zero or many of them.
 * One `SimQuota` describes either voice minutes, or certain amount of internet traffic - distinct by `type`.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "sim_quota")
public class SimQuota {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    @Builder.Default
    private SimQuotaStatus status = SimQuotaStatus.ENABLED;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private SimQuotaType type;

    @Column(name = "balance")
    @NotNull
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "sim_id")
    @NotNull
    private SimCard simCard;

    @Column(name = "date_created")
    private OffsetDateTime dateCreated;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        var now = OffsetDateTime.now();
        this.dateCreated = now;
        lastUpdated = now;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = OffsetDateTime.now();
    }
}
