package uk.ac.ebi.spot.goci.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 20/04/2016.
 *
 * @author emma
 *         <p>
 *         Am abstract class used to represent a summary of a validated object and its errors
 */
abstract public class ValidationSummary {

    private Collection<ValidationError> errors = new ArrayList<>();

    public Collection<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(Collection<ValidationError> errors) {
        this.errors = errors;
    }
}
