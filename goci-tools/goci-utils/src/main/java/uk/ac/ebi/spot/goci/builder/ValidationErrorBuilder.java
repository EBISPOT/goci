package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.ValidationError;

/**
 * Created by emma on 22/04/2016.
 *
 * @author emma
 *         <p>
 *         Builder class for ValidationError
 */
public class ValidationErrorBuilder {

    private ValidationError validationError = new ValidationError();

    public ValidationErrorBuilder setField(String field) {
        validationError.setField(field);
        return this;
    }

    public ValidationErrorBuilder setError(String error) {
        validationError.setError(error);
        return this;
    }

    public ValidationError build() {
        return validationError;
    }

}
