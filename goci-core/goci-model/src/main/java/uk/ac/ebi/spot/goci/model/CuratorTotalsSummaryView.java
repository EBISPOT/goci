package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;

/**
 * Created by emma on 22/09/2015.
 * <p>
 * Model of the CURATOR_TOTALS_SUMMARY_VIEW table
 */
@Entity
public class CuratorTotalsSummaryView {

    private String year;

    private Integer month;

    private String curator;

    private Integer curatorMonthlyTotals;

    private String curationStatus;

    // JPA no-args constructor
    public CuratorTotalsSummaryView() {
    }

    public CuratorTotalsSummaryView(String year, Integer month, String curator, Integer curatorMonthlyTotals, String curationStatus) {
        this.year = year;
        this.month = month;
        this.curator = curator;
        this.curatorMonthlyTotals = curatorMonthlyTotals;
        this.curationStatus = curationStatus;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getCurator() {
        return curator;
    }

    public void setCurator(String curator) {
        this.curator = curator;
    }

    public Integer getCuratorMonthlyTotals() {
        return curatorMonthlyTotals;
    }

    public void setCuratorMonthlyTotals(Integer curatorMonthlyTotals) {
        this.curatorMonthlyTotals = curatorMonthlyTotals;
    }

    public String getCurationStatus() {
        return curationStatus;
    }

    public void setCurationStatus(String curationStatus) {
        this.curationStatus = curationStatus;
    }
}
