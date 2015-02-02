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

    public CuratorTotalsTableRow() {
    }


    public CuratorTotalsTableRow(String curator, Integer outstandingQueries, Integer curatorTotalEntries, Integer curatorTotalStudies, Integer monthlyTotalEntries, Integer monthlyTotalStudies, String year, String month) {
        this.curator = curator;
        this.outstandingQueries = outstandingQueries;
        this.curatorTotalEntries = curatorTotalEntries;
        this.curatorTotalStudies = curatorTotalStudies;
        this.monthlyTotalEntries = monthlyTotalEntries;
        this.monthlyTotalStudies = monthlyTotalStudies;
        this.year = year;
        this.month = month;
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
}
