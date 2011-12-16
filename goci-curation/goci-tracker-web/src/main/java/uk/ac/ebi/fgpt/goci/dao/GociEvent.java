package uk.ac.ebi.fgpt.goci.dao;

import uk.ac.ebi.fgpt.goci.model.GociStudy;

/**
 * An event that encapsulates a change that may occur to a GociStudy
 *
 * @author Tony Burdett
 * @date 01/11/11
 */
public class GociEvent {
    private GociStudy study;
    private Change change;

    public GociEvent(GociStudy study, Change change) {
        this.study = study;
        this.change = change;
    }

    public GociStudy getStudy() {
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
