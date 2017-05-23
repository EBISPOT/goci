package uk.ac.ebi.spot.goci.model;

import java.util.Collection;

/**
 * Created by emma on 20/04/2016.
 *
 * @author emma
 *         <p>
 *         Class used to represent a newly created row and any errors attahced
 */
public class RowValidationSummary {

    private AssociationUploadRow row;

    private Collection<ValidationError> errors;

    public AssociationUploadRow getRow() {
        return row;
    }

    public void setRow(AssociationUploadRow row) {
        this.row = row;
    }

    public Collection<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(Collection<ValidationError> errors) {
        this.errors = errors;
    }
}
