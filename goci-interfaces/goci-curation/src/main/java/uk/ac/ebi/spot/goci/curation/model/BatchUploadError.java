package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Model class used to process errors generated after a failed upload of file containing SNP association values
 */
public class BatchUploadError {

    private String row;

    private String column;

    private String error;

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
