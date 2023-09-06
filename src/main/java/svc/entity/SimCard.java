package svc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@Builder
@Table(name = "sim_card")
public class SimCard {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    @Builder.Default
    private SimCardStatus status = SimCardStatus.ENABLED;

    /**
     * Like "79001234567" for Russia.
     */
    @Column(length = 15)
    @NotNull
    private String msisdn;

    @Column(name = "date_created")
    private OffsetDateTime dateCreated;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    public SimCard() {
    }

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
