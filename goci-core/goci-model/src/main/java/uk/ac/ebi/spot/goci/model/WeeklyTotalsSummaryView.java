package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;

/**
 * Created by dwelter on 15/04/16.
 */

@Entity
public class WeeklyTotalsSummaryView extends TotalsSummaryView{

    private Integer week;

    private Integer weeklyTotal;

    // JPA no-args constructor
    public WeeklyTotalsSummaryView() {
    }

    public WeeklyTotalsSummaryView(Integer year,
                                    String curator,
                                    Integer curatorTotal,
                                    String curationStatus,
                                    Integer month,
                                    Integer weeklyTotal) {
        super(year, curator, curatorTotal, curationStatus);
        this.week = week;
        this.weeklyTotal = weeklyTotal;
    }

    public Integer getweek() {
        return week;
    }

    public void setweek(Integer week) {
        this.week = week;
    }

    public Integer getWeeklyTotal() {
        return weeklyTotal;
    }

    public void setWeeklyTotal(Integer weeklyTotal) {
        this.weeklyTotal = weeklyTotal;
    }
}
