package uk.ac.ebi.spot.goci.model;

/**
 * Created by emma on 20/04/2016.
 *
 * @author emma
 *         <p>
 *         A class to capture validation errors
 */
public class ValidationError {

    private String field;

    private String error;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
