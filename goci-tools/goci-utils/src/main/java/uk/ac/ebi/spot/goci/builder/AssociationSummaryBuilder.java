package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.ValidationError;

import java.util.Collection;

/**
 * Created by emma on 16/06/2016.
 *
 * @author emma
 *         <p>
 *         Association Summary builder used during testing
 */
public class AssociationSummaryBuilder {

    private AssociationSummary associationSummary = new AssociationSummary();

    public AssociationSummaryBuilder setAssociation(Association association) {
        associationSummary.setAssociation(association);
        return this;
    }

    public AssociationSummaryBuilder setErrors(Collection<ValidationError> errors) {
        associationSummary.setErrors(errors);
        return this;
    }

    public AssociationSummaryBuilder setRowNumber(Integer rowNumber) {
        associationSummary.setRowNumber(rowNumber);
        return this;
    }

    public AssociationSummary build(){
        return associationSummary;
    }

}
