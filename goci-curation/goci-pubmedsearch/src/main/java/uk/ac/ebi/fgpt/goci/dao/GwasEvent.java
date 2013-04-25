package uk.ac.ebi.fgpt.goci.dao;

import uk.ac.ebi.fgpt.goci.model.GwasStudy;

/**
 * An event that encapsulates a change that may occur to a GwasStudy
 *
 * @author Tony Burdett
 * Date 01/11/11
 */
public class GwasEvent {
    private GwasStudy study;
    private Change change;

    public GwasEvent(GwasStudy study, Change change) {
        this.study = study;
        this.change = change;
    }

    public GwasStudy getStudy() {
        return study;
    }

    public Change getChangeType() {
        return change;
    }

    public enum Change {
        OWNER_UPDATE,
        STATE_UPDATE,
        ELIGIBILITY_UPDATE
    }
}
