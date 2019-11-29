package ftn.tim16.ClinicalCentreSystem.model;

import ftn.tim16.ClinicalCentreSystem.enumeration.TimeOffStatus;
import ftn.tim16.ClinicalCentreSystem.enumeration.TimeOffType;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class TimeOffNurse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TimeOffType type;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private DateTimeInterval interval;

    @Enumerated(EnumType.STRING)
    private TimeOffStatus status;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Nurse nurse;

    public Long getId() {
        return id;
    }

    public TimeOffType getType() {
        return type;
    }

    public void setType(TimeOffType type) {
        this.type = type;
    }

    public DateTimeInterval getInterval() {
        return interval;
    }

    public void setInterval(DateTimeInterval interval) {
        this.interval = interval;
    }

    public TimeOffStatus getStatus() {
        return status;
    }

    public void setStatus(TimeOffStatus status) {
        this.status = status;
    }

    public Nurse getNurse() {
        return nurse;
    }

    public void setNurse(Nurse nurse) {
        this.nurse = nurse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeOffNurse timeOffNurse = (TimeOffNurse) o;
        if (timeOffNurse.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, timeOffNurse.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
