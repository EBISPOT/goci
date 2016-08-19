package uk.ac.ebi.spot.goci.model;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Class used to represent errors generated after validation of an upload spreadsheet containing association
 *         values
 */
public class ValidationError {

    private String field;

    private String error;

    private Boolean warning = false;

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

    public Boolean getWarning() {
        return warning;
    }

    public void setWarning(Boolean warning) {
        this.warning = warning;
    }
}
