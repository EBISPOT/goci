package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by emma on 08/06/2016.
 * @author emma
 *
 *  Model of WEEKLY_PROGRESS_VIEW table
 */
@Entity
public class WeeklyProgressView {

    @Id
    private Long id;

    private Date weekStartDate;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Integer numberOfStudies;

    // JPA no-args constructor
    public WeeklyProgressView() {
    }

    public WeeklyProgressView(Long id, Integer numberOfStudies, EventType eventType, Date weekStartDate) {
        this.id = id;
        this.numberOfStudies = numberOfStudies;
        this.eventType = eventType;
        this.weekStartDate = weekStartDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(Date weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Integer getNumberOfStudies() {
        return numberOfStudies;
    }

    public void setNumberOfStudies(Integer numberOfStudies) {
        this.numberOfStudies = numberOfStudies;
    }
}
