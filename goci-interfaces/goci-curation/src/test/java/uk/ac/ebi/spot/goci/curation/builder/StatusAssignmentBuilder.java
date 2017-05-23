package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.curation.model.StatusAssignment;

/**
 * Created by emma on 13/06/2016.
 *
 * @author emma
 *         <p>
 *         Status assignment builder used during testing
 */
public class StatusAssignmentBuilder {

    private StatusAssignment statusAssignment = new StatusAssignment();

    public StatusAssignmentBuilder setStatusId(Long statusId) {
        statusAssignment.setStatusId(statusId);
        return this;
    }

    public StatusAssignmentBuilder setUri(String uri) {
        statusAssignment.setUri(uri);
        return this;
    }

    public StatusAssignment build() {
        return statusAssignment;
    }
}