package uk.ac.ebi.spot.goci.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Class used to represent errors generated after validation of an upload spreadsheet containing association
 *         values
 *         Added field typeError: this field is used to define if the error is data or xls issue.
 */
public class ValidationError {

    private String field;

    private String error;

    private Boolean warning = false;

    private String typeError = "data"; // data or excel

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

    public String getTypeError() { return typeError; }

    public void setTypeError(String typeError) { this.typeError = typeError; }

    @Autowired
    public ValidationError() {}

    @Autowired
    public ValidationError(String field, String error, Boolean warning, String typeError) {
        this.setField(field);
        this.setError(error);
        this.setWarning(warning);
        this.setTypeError(typeError);
    }

}
