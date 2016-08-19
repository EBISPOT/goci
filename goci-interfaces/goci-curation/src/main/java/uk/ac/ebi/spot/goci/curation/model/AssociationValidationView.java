package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 29/06/2016.
 *
 * @author emma
 *         <p>
 *         Model object used to display errors and warnings in an association form and return this to view
 */
public class AssociationValidationView {

    private String field;

    private String errorMessage;

    private Boolean warning;

    public AssociationValidationView(String field, String errorMessage, Boolean warning) {
        this.field = field;
        this.errorMessage = errorMessage;
        this.warning = warning;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getWarning() {
        return warning;
    }

    public void setWarning(Boolean warning) {
        this.warning = warning;
    }
}