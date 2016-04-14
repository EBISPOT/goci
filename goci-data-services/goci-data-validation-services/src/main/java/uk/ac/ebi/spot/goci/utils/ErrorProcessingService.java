package uk.ac.ebi.spot.goci.utils;

import uk.ac.ebi.spot.goci.model.AssociationValidationError;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 14/04/2016.
 *
 * @author emma
 *         <p>
 *         Utility service to format errors for return to user
 */
public class ErrorProcessingService {
    /**
     * Create error object
     *
     * @param message       Error message
     * @param columnChecked Name of the column checked
     */
    public static AssociationValidationError createError(String message, String columnChecked) {
        AssociationValidationError error = new AssociationValidationError();

        // If there is an error create a fully formed object
        if (message != null) {
            error.setColumnName(columnChecked);
            error.setError(message);
        }
        return error;
    }

    /**
     * Check error objects created to ensure we only return those with an actual message and location
     *
     * @param errors Errors to be checked
     * @return validErrors list of errors with message and location
     */
    public static Collection<AssociationValidationError> checkForValidErrors(Collection<AssociationValidationError> errors) {
        Collection<AssociationValidationError> validErrors = new ArrayList<>();
        for (AssociationValidationError error : errors) {
            if (error.getError() != null) {
                validErrors.add(error);
            }
        }
        return validErrors;
    }
}