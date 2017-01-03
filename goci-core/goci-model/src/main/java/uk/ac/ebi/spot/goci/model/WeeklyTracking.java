package uk.ac.ebi.spot.goci.model;

import javax.persistence.*;
import java.sql.Date;


/**
 * Created by Cinzia on 9/11/16.
 *
 * @author Cinzia
 *         <p>
 *         Model object representing weekly tracking view and its attributes
 */
@Entity
public class WeeklyTracking {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    private String pubmedId;

    private Integer week = 0;

    private Integer year = 0;

    private String status;

    private Date eventDate;


    // JPA no-args constructor
    public WeeklyTracking() {
    }

    public WeeklyTracking(String pubmedId) { this.pubmedId = pubmedId; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Study getStudy() { return study; }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Long getStudyId() { return study.getId(); }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    // If two entries have the same week and year
    public boolean equalWeekYear(WeeklyTracking weekTrackingObject) {
        if (this == weekTrackingObject) return true;

        if ((this.getWeek() == weekTrackingObject.getWeek()) && (this.getYear()== weekTrackingObject.getYear())) {
            return true;
        }

        return false;
    }

    // If the entity was created before the parameter
    public boolean beforeWeekYear(WeeklyTracking weekTrackingObject) {
        if (this == weekTrackingObject) return true;

        if (this.getYear() > weekTrackingObject.getYear()) {
            return false;
        }

        if (this.getYear().compareTo(weekTrackingObject.getYear()) == 0) {
            if (this.getWeek() < weekTrackingObject.getWeek()) { return true; }
            else { return false; }
        }

        return true;
    }


}
