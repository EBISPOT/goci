package uk.ac.ebi.spot.goci.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Model class used to represent a newly created association and any errors attahced
 */
public class AssociationSummary {

    private Association association;

    private Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public Collection<AssociationValidationError> getAssociationValidationErrors() {
        return associationValidationErrors;
    }

    public void setAssociationValidationErrors(Collection<AssociationValidationError> associationValidationErrors) {
        this.associationValidationErrors = associationValidationErrors;
    }
}
