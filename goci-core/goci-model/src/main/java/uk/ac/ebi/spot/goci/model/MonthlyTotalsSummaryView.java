package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;

/**
 * Created by emma on 22/09/2015.
 * <p>
 * Model of the MONTHLY_TOTALS_SUMMARY_VIEW table
 */
@Entity
public class MonthlyTotalsSummaryView extends TotalsSummaryView {

    private Integer month;

    private Integer monthlyTotal;

    // JPA no-args constructor
    public MonthlyTotalsSummaryView() {
    }

    public MonthlyTotalsSummaryView(Integer year,
                                    String curator,
                                    Integer curatorTotal,
                                    String curationStatus,
                                    Integer month,
                                    Integer monthlyTotal) {
        super(year, curator, curatorTotal, curationStatus);
        this.month = month;
        this.monthlyTotal = monthlyTotal;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getMonthlyTotal() {
        return monthlyTotal;
    }

    public void setMonthlyTotal(Integer monthlyTotal) {
        this.monthlyTotal = monthlyTotal;
    }
}
