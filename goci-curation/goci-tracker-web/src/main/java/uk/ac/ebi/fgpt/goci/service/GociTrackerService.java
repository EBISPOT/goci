package uk.ac.ebi.fgpt.goci.service;

import uk.ac.ebi.fgpt.goci.model.GociStudy;

/**
 * A service for tracking all studies entered into the GOCI Tracking system.
 *
 * @author Tony Burdett
 * Date 26/10/11
 */
public interface GociTrackerService {
    /**
     * Enters a new study into the tracking system.
     *
     * @param study the study to be entered for tracking
     */
    void enterStudy(GociStudy study);

    /**
     * Determines if a GociStudy object with the given PubMed ID has already been entered into the tracking system
     *
     * @param pubmedID the PubMed ID of the study to check
     * @return true if a GociStudy relating to this PubMed ID has already been entered.
     */
    boolean isStudyEntered(String pubmedID);
}
