package uk.ac.ebi.fgpt.goci.service;

import uk.ac.ebi.fgpt.goci.exception.InsufficientPrivilegesException;
import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.model.GociUser;

/**
 * A service that allows tracked studies to be curated with a series of updates that can be applied by the user.
 *
 * @author Tony Burdett
 * @date 26/10/11
 */
public interface GociCurationService {
    /**
     * Updates the current state of the supplied study.
     *
     * @param studyID      the ID of the study to update
     * @param updatedState the new state the study should have
     * @param user the user who is carrying out this update
     */
    void updateState(String studyID, GociStudy.State updatedState, GociUser user)
            throws InsufficientPrivilegesException;

    /**
     * Updates the current eligibility flag on the supplied study.
     *
     * @param studyID            the ID of the study to update
     * @param updatedEligibility the new eligibility flag for this study
     * @param user the user who is carrying out this update
     */
    void updateEligibility(String studyID, GociStudy.Eligibility updatedEligibility, GociUser user)
            throws InsufficientPrivilegesException;

    /**
     * Assigns the supplied study to a different owner.
     *
     * @param studyID      the ID of the study to update
     * @param updatedOwner the new owner of this study
     * @param user the user who is carrying out this update
     */
    void assignOwner(String studyID, GociUser updatedOwner, GociUser user) throws InsufficientPrivilegesException;
}
