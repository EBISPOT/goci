package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by emma on 08/06/2016.
 *
 * @author emma
 *         <p>
 *         Model of WEEKLY_PROGRESS_VIEW table
 */
@Entity
public class WeeklyProgressView {

    @Id
    private Long id;

    private Date weekStartDate;

    private Long studyId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    // JPA no-args constructor
    public WeeklyProgressView() {
    }

    public WeeklyProgressView(Long id,
                              Date weekStartDate,
                              Long studyId,
                              EventType eventType,
                              Integer numberOfStudies) {
        this.id = id;
        this.weekStartDate = weekStartDate;
        this.studyId = studyId;
        this.eventType = eventType;
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

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
}
