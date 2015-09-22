package uk.ac.ebi.spot.goci.model;

import javax.persistence.Entity;

/**
 * Created by emma on 22/09/2015.
 */
@Entity
public class YearlyCuratorTotalsSummaryView extends TotalsSummaryView {

    // JPA no-args constructor
    public YearlyCuratorTotalsSummaryView() {
    }

    public YearlyCuratorTotalsSummaryView(String year, String curator, Integer total, String curationStatus) {
        super(year, curator, total, curationStatus);
    }
}
