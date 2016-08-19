package uk.ac.ebi.spot.goci.service;

import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.ValidationError;

import java.util.Collection;

/**
 * Created by emma on 25/04/2016.
 *
 * @author emma
 *         <p>
 *         Service that acts as an entry point for validation services. Two methods are available that represent the
 *         various levels and stages of validation performed
 */
public interface ValidationService {

    Collection<ValidationError> runAssociationValidation(Association association,
                                                         String validationLevel);

    Collection<ValidationError> runRowValidation(AssociationUploadRow row);
}
