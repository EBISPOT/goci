package uk.ac.ebi.spot.goci.model;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by Cinzia on 01/12/16.
 *
 * @author Cinzia
 *         <p>
 *         Model object representing a curator tracking and its attributes
 */
@Entity
public class CuratorTracking {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    private String curatorName;

    private String pubmedId;

    private Integer week;

    private Integer year;

    private String levelCuration;

    private Date levelCurationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public String getCurator() {
        return curatorName;
    }

    public void setCurator(String curatorName) { this.curatorName = curatorName; }

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

    public String getLevelCuration() {
        return levelCuration;
    }

    public void setLevelCuration(String levelCuration) {
        this.levelCuration = levelCuration;
    }

    public Date getLevelCurationDate() {
        return levelCurationDate;
    }

    public void setLevelCurationDate(Date levelCurationDate) {
        this.levelCurationDate = levelCurationDate;
    }
}
