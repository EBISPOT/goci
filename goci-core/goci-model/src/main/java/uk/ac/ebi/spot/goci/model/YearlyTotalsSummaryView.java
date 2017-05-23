package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;

/**
 * Created by emma on 22/09/2015.
 *
 * @author emma
 *         <p>
 *         Model of YEARLY_TOTALS_SUMMARY_VIEW table
 */
@Entity
public class YearlyTotalsSummaryView extends TotalsSummaryView {

    // JPA no-args constructor
    public YearlyTotalsSummaryView() {
    }

    public YearlyTotalsSummaryView(Integer year, String curator, Integer curatorTotal, String curationStatus) {
        super(year, curator, curatorTotal, curationStatus);
    }
}
