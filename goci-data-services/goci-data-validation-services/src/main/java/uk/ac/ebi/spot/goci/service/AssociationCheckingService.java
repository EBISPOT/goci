package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;

import java.util.Collection;

/**
 * Created by emma on 01/04/2016.
 *
 * @author emma
 *         <p>
 *         Interface that defines method(s) to run error ckecking of an association and then return a collection of
 *         errors.
 */
public interface AssociationCheckingService {
    Collection<AssociationValidationError> runChecks(Association association, CheckingService checkingService);

    /**
     * Check if association is an OR or BETA type association
     *
     * @param association Association to check
     */
    default String determineIfAssociationIsOrType(Association association) {

        String effectType = "none";
        if (association.getBetaNum() != null) {
            effectType = "beta";
        }
        else {
            if (association.getOrPerCopyNum() != null) {
                effectType = "or";
            }
        }
        return effectType;
    }
}
