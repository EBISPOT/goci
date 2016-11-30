package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 05/08/2016.
 *
 * @author emma
 */
public class AncestryEventView extends EventView {

    private String ancestrySummary;

    public AncestryEventView(String event,
                             Date eventDate,
                             Long trackableId,
                             String userEmail,
                             String ancestrySummary) {
        super(event, eventDate, trackableId, userEmail);
        this.ancestrySummary = ancestrySummary;
    }

    public String getAncestrySummary() {
        return ancestrySummary;
    }

    public void setAncestrySummary(String ancestrySummary) {
        this.ancestrySummary = ancestrySummary;
    }
}
