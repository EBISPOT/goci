package uk.ac.ebi.spot.goci.curation.model;

import java.util.Date;

/**
 * Created by emma on 22/07/2016.
 * @author emma
 */
public class AssociationEventView extends EventView {

    private String associationSummary;

    public AssociationEventView(String event,
                                Date eventDate,
                                Long trackableId,
                                String userEmail,
                                String associationSummary) {
        super(event, eventDate, trackableId, userEmail);
        this.associationSummary = associationSummary;
    }

    public String getAssociationSummary() {
        return associationSummary;
    }

    public void setAssociationSummary(String associationSummary) {
        this.associationSummary = associationSummary;
    }
}
