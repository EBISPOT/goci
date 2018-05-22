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
    private String pubmedId;
    private String accessionId;
    private String author;

//    private Integer monthlyTotal;

    // JPA no-args constructor
    public MonthlyTotalsSummaryView() {
    }

    public MonthlyTotalsSummaryView(Integer year,
                                    String curator,
                                    Integer curatorTotal,
                                    String curationStatus,
                                    Integer month
//                                    Integer monthlyTotal
                                    ) {
        super(year, curator, curatorTotal, curationStatus);
        this.month = month;
//        this.monthlyTotal = monthlyTotal;
        this.pubmedId = pubmedId;
        this.accessionId = accessionId;
        this.author = author;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    public String getAccessionId() {
        return accessionId;
    }

    public void setAccessionId(String accessionId) {
        this.accessionId = accessionId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

//    public Integer getMonthlyTotal() {
//        return monthlyTotal;
//    }
//
//    public void setMonthlyTotal(Integer monthlyTotal) {
//        this.monthlyTotal = monthlyTotal;
//    }
}
