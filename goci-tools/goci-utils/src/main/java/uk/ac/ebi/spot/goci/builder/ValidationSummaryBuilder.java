package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.RowValidationSummary;
import uk.ac.ebi.spot.goci.model.ValidationSummary;

import java.util.Collection;

/**
 * Created by emma on 16/06/2016.
 *
 * @author emma
 *         <p>
 *         Validation summary builder used in testing
 */
public class ValidationSummaryBuilder {

    private ValidationSummary validationSummary = new ValidationSummary();

    public ValidationSummaryBuilder setAssociationSummaries(Collection<AssociationSummary> associationSummaries) {
        validationSummary.setAssociationSummaries(associationSummaries);
        return this;
    }

    public ValidationSummaryBuilder setRowValidationSummaries(Collection<RowValidationSummary> rowValidationSummaries) {
        validationSummary.setRowValidationSummaries(rowValidationSummaries);
        return this;
    }

    public ValidationSummary build() {
        return validationSummary;
    }
}
