package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;

import java.util.Collection;

/**
 * Created by emma on 01/04/2016.
 *
 * @author emma
 *         <p>
 *         Interface that defines method(s) to run error ckecking of an association and then return a collection of errors.
 */
public interface AssociationCheckingService {
    Collection<AssociationValidationError> runChecks(Association association, String effectType);
}
