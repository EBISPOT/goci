package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.RowValidationSummary;
import uk.ac.ebi.spot.goci.model.ValidationError;

import java.util.Collection;

/**
 * Created by emma on 16/06/2016.
 *
 * @author emma
 *         <p>
 *         Row summary builder used in testing
 */
public class RowValidationSummaryBuilder {

    private RowValidationSummary rowValidationSummary = new RowValidationSummary();

    public RowValidationSummaryBuilder setRow(AssociationUploadRow row) {
        rowValidationSummary.setRow(row);
        return this;
    }

    public RowValidationSummaryBuilder setErrors(Collection<ValidationError> errors) {
        rowValidationSummary.setErrors(errors);
        return this;
    }

    public RowValidationSummary build() {
        return rowValidationSummary;
    }
}
