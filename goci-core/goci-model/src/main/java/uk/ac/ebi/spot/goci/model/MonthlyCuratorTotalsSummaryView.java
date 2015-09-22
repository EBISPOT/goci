package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;

/**
 * Created by emma on 22/09/2015.
 * <p>
 * Model of the CURATOR_TOTALS_SUMMARY_VIEW table
 */
@Entity
public class MonthlyCuratorTotalsSummaryView extends TotalsSummaryView {

    private Integer month;

    // JPA no-args constructor
    public MonthlyCuratorTotalsSummaryView() {
    }

    public MonthlyCuratorTotalsSummaryView(String year, String curator, Integer total, String curationStatus, Integer month) {
        super(year, curator, total, curationStatus);
        this.month = month;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}
