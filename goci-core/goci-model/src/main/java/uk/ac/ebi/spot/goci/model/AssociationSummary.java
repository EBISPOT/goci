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
}
