package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 14/06/2016.
 *
 * @author emma
 *         <p>
 *         Model object used to store errors in an association upload file and return this to view
 */
public class AssociationUploadErrorView {

    private Integer row;

    private String columnName;

    private String error;

    public AssociationUploadErrorView(Integer row, String columnName, String error) {
        this.row = row;
        this.columnName = columnName;
        this.error = error;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
