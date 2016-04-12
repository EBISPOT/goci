package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.AssociationValidationError;

/**
 * Created by emma on 30/03/2016.
 *
 * @author emma
 *         <p>
 *         Builder for BatchUploadError
 */
public class BatchUploadErrorBuilder {

    private AssociationValidationError associationValidationError = new AssociationValidationError();

    public BatchUploadErrorBuilder setRow(Integer row) {
        associationValidationError.setRow(row);
        return this;
    }

    public BatchUploadErrorBuilder setColumnName(String columnName) {
        associationValidationError.setColumnName(columnName);
        return this;
    }

    public BatchUploadErrorBuilder setError(String error) {
        associationValidationError.setError(error);
        return this;
    }

    public AssociationValidationError build() {
        return associationValidationError;
    }
}