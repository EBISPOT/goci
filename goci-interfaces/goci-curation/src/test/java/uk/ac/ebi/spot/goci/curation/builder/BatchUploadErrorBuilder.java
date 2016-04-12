package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.BatchUploadError;

/**
 * Created by emma on 30/03/2016.
 *
 * @author emma
 *         <p>
 *         Builder for BatchUploadError
 */
public class BatchUploadErrorBuilder {

    private BatchUploadError batchUploadError = new BatchUploadError();

    public BatchUploadErrorBuilder setRow(Integer row) {
        batchUploadError.setRow(row);
        return this;
    }

    public BatchUploadErrorBuilder setColumnName(String columnName) {
        batchUploadError.setColumnName(columnName);
        return this;
    }

    public BatchUploadErrorBuilder setError(String error) {
        batchUploadError.setError(error);
        return this;
    }

    public BatchUploadError build() {
        return batchUploadError;
    }
}