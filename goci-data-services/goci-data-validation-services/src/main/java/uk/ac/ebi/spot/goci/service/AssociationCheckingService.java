package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.ValidationError;

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
    Collection<ValidationError> runChecks(Association association, ValidationChecksBuilder validationChecksBuilder);

    /**
     * Check if association is an OR or BETA type association
     *
     * @param association Association to check
     */
    default String determineIfAssociationIsOrType(Association association) {

        String effectType = "none";
        if (association.getOrPerCopyNum() != null) {
            effectType = "or";
        }
        else {
            if (association.getBetaNum() != null) {
                effectType = "beta";
            }
        }
        return effectType;
    }
}
