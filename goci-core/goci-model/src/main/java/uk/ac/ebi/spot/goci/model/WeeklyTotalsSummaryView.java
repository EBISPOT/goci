package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

/**
 * Created by dwelter on 15/04/16.
 */

@Entity
public class WeeklyTotalsSummaryView{ //extends TotalsSummaryView{

    @Id
    private Long id;

    private Date week;

    private Integer weeklyStudies;

    private Integer weeklyEntries;

    // JPA no-args constructor
    public WeeklyTotalsSummaryView() {
    }

    public WeeklyTotalsSummaryView(Date week,
                                    Integer weeklyStudies,
                                    Integer weeklyEntries) {
        this.week = week;
        this.weeklyStudies = weeklyStudies;
        this.weeklyEntries = weeklyEntries;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getWeek() {
        return week;
    }

    public void setWeek(Date week) {
        this.week = week;
    }

    public Integer getWeeklyStudies() {
        return weeklyStudies;
    }

    public void setWeeklyStudies(Integer weeklyStudies) {
        this.weeklyStudies = weeklyStudies;
    }

    public Integer getWeeklyEntries() {
        return weeklyEntries;
    }

    public void setWeeklyEntries(Integer weeklyEntries) {
        this.weeklyEntries = weeklyEntries;
    }
}
