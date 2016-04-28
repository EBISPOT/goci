package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;
import java.sql.Date;

/**
 * Created by dwelter on 15/04/16.
 */

@Entity
public class WeeklyTotalsSummaryView extends TotalsSummaryView{

    private Date week;

    private Integer weeklyStudies;

    private Integer weeklyEntries;

    // JPA no-args constructor
    public WeeklyTotalsSummaryView() {
    }

    public WeeklyTotalsSummaryView(Date week,
//                                    String curator,
//                                    Integer curatorTotal,
//                                    String curationStatus,
//                                    Integer month,
                                    Integer weeklyStudies,
                                    Integer weeklyEntries) {
//        super(year, curator, curatorTotal, curationStatus);
        this.week = week;
        this.weeklyStudies = weeklyStudies;
        this.weeklyEntries = weeklyEntries;
    }

    public Date getweek() {
        return week;
    }

    public void setweek(Date week) {
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
