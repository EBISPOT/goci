package uk.ac.ebi.spot.goci.utils;

import uk.ac.ebi.spot.goci.model.ValidationError;

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
    public static ValidationError createError(String message, String columnChecked) {
        ValidationError error = new ValidationError();

        // If there is an error create a fully formed object
        if (message != null) {
            error.setField(columnChecked);
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
    public static Collection<ValidationError> checkForValidErrors(Collection<ValidationError> errors) {
        Collection<ValidationError> validErrors = new ArrayList<>();
        for (ValidationError error : errors) {
            if (error.getError() != null) {
                validErrors.add(error);
            }
        }
        return validErrors;
    }
}