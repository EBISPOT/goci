package uk.ac.ebi.spot.goci.model;

import java.util.Collection;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Class used to represent a newly created association and any errors attahced
 */
public class AssociationSummary {

    // Row  number is only set if creating an association from an upload file
    private Integer rowNumber;

    private Association association;

    private Collection<ValidationError> errors;

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public Collection<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(Collection<ValidationError> errors) {
        this.errors = errors;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }
}
