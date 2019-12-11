package svc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "sim_card")
public class SimCard {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private SimCardStatus status = SimCardStatus.ENABLED;

    /**
     * Like "79001234567" for Russia.
     */
    @Column(length = 15)
    @NotNull
    private String msisdn;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "last_updated")
    private Date lastUpdated;

    public SimCard() {
    }

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.dateCreated = now;
        lastUpdated = now;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public SimCardStatus getStatus() {
        return status;
    }

    public void setStatus(SimCardStatus status) {
        this.status = status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimCard simCard = (SimCard) o;
        return id == simCard.id &&
                status == simCard.status &&
                Objects.equals(msisdn, simCard.msisdn) &&
                Objects.equals(dateCreated, simCard.dateCreated) &&
                Objects.equals(lastUpdated, simCard.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, msisdn, dateCreated, lastUpdated);
    }
}
