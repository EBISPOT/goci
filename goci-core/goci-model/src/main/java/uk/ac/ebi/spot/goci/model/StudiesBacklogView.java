package uk.ac.ebi.spot.goci.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by cinzia on 31/10/2016.
 */

@Entity
public class StudiesBacklogView {

    @Id
    private Long id;

    private String eventDay;

    private Integer studyCreation;

    private Integer studyPublished;

    // JPA no-args constructor
    public StudiesBacklogView() {
    }

    public StudiesBacklogView(String eventDay,
                                   Integer studyCreation,
                                   Integer studyPublished) {
        this.eventDay = eventDay;
        this.studyCreation = studyCreation;
        this.studyPublished = studyPublished;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventDay() {
        return eventDay;
    }

    public void setEventDay(String eventDay) {
        this.eventDay = eventDay;
    }

    public Integer getStudyCreation() {
        return studyCreation;
    }

    public void setStudyCreation(Integer studyCreation) {
        this.studyCreation = studyCreation;
    }

    public Integer getStudyPublished() {
        return studyPublished;
    }

    public void setStudyPublished(Integer studyPublished) {
        this.studyPublished = studyPublished;
    }
}
