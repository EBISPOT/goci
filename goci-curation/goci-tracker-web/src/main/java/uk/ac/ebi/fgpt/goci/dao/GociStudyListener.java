package uk.ac.ebi.fgpt.goci.dao;

import java.util.EventListener;

/**
 * A listener interface that can react to changes in GociStudies.
 *
 * @author Tony Burdett
 * @date 01/11/11
 */
public interface GociStudyListener extends EventListener {
    /**
     * Called whenever a study is updated with new information.
     *
     * @param evt the event describing the change
     */
    public void studyUpdated(GociEvent evt);
}
