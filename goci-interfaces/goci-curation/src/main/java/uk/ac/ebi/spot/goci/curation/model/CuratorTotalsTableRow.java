package uk.ac.ebi.spot.goci.curation.model;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Curator;

/**
 * Created by emma on 29/01/15.
 */
@Service
public class CuratorTotalsTableRow {

    private String curator;
    private String month;
    private String year;
    private Integer monthlyTotalStudies;
    private Integer monthlyTotalEntries;
    private Integer curatorTotalStudies;
    private Integer curatorTotalEntries;
    private Integer outstandingQueries;
    private String period;

    public CuratorTotalsTableRow() {
    }


    public CuratorTotalsTableRow(String period, String curator, String month, String year, Integer monthlyTotalStudies, Integer monthlyTotalEntries, Integer curatorTotalStudies, Integer curatorTotalEntries, Integer outstandingQueries) {
        this.period = period;
        this.curator = curator;
        this.month = month;
        this.year = year;
        this.monthlyTotalStudies = monthlyTotalStudies;
        this.monthlyTotalEntries = monthlyTotalEntries;
        this.curatorTotalStudies = curatorTotalStudies;
        this.curatorTotalEntries = curatorTotalEntries;
        this.outstandingQueries = outstandingQueries;
    }

    public String getCurator() {
        return curator;
    }

    public void setCurator(String curator) {
        this.curator = curator;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getMonthlyTotalStudies() {
        return monthlyTotalStudies;
    }

    public void setMonthlyTotalStudies(Integer monthlyTotalStudies) {
        this.monthlyTotalStudies = monthlyTotalStudies;
    }

    public Integer getMonthlyTotalEntries() {
        return monthlyTotalEntries;
    }

    public void setMonthlyTotalEntries(Integer monthlyTotalEntries) {
        this.monthlyTotalEntries = monthlyTotalEntries;
    }

    public Integer getCuratorTotalStudies() {
        return curatorTotalStudies;
    }

    public void setCuratorTotalStudies(Integer curatorTotalStudies) {
        this.curatorTotalStudies = curatorTotalStudies;
    }

    public Integer getCuratorTotalEntries() {
        return curatorTotalEntries;
    }

    public void setCuratorTotalEntries(Integer curatorTotalEntries) {
        this.curatorTotalEntries = curatorTotalEntries;
    }

    public Integer getOutstandingQueries() {
        return outstandingQueries;
    }

    public void setOutstandingQueries(Integer outstandingQueries) {
        this.outstandingQueries = outstandingQueries;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
