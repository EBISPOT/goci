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

    private Date weekStartDay;

    private Long studyId;

    //@Enumerated(EnumType.STRING)
    private String eventType;

    // JPA no-args constructor
    public WeeklyProgressView() {
    }

    public WeeklyProgressView(Long id,
                              Date weekStartDay,
                              Long studyId,
                              String eventType,
                              Integer numberOfStudies) {
        this.id = id;
        this.weekStartDay = weekStartDay;
        this.studyId = studyId;
        this.eventType = eventType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getWeekStartDay() {
        return weekStartDay;
    }

    public void setWeekStartDay(Date weekStartDay) {
        this.weekStartDay = weekStartDay;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
}
