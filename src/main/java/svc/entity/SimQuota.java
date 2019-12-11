package svc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * A single quota (packet) available to a SIM.
 * There can be zero or many of them.
 * One `SimQuota` describes either voice minutes, or certain amount of internet traffic - distinct by `type`.
 */
@Entity
@Table(name = "sim_quota")
public class SimQuota {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private SimQuotaStatus status = SimQuotaStatus.ENABLED;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private SimQuotaType type;

    @Column(name = "balance")
    @NotNull
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "end_date")
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "sim_id")
    @NotNull
    private SimCard simCard;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "last_updated")
    private Date lastUpdated;

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

    public SimQuotaStatus getStatus() {
        return status;
    }

    public void setStatus(SimQuotaStatus status) {
        this.status = status;
    }

    public SimQuotaType getType() {
        return type;
    }

    public void setType(SimQuotaType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public SimCard getSimCard() {
        return simCard;
    }

    public void setSimCard(SimCard simCard) {
        this.simCard = simCard;
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
        SimQuota quota = (SimQuota) o;
        return id == quota.id &&
                status == quota.status &&
                type == quota.type &&
                Objects.equals(balance, quota.balance) &&
                Objects.equals(endDate, quota.endDate) &&
                Objects.equals(simCard, quota.simCard) &&
                Objects.equals(dateCreated, quota.dateCreated) &&
                Objects.equals(lastUpdated, quota.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, type, balance, endDate, simCard, dateCreated, lastUpdated);
    }

    @Override
    public String toString() {
        return "SimQuota{" +
                "id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", balance=" + balance +
                ", endDate=" + endDate +
                ", simCard=" + simCard +
                ", dateCreated=" + dateCreated +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
