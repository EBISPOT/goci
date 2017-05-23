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

    private Boolean warning;

    private String typeError;


    public AssociationUploadErrorView(Integer row, String columnName, String error, Boolean warning, String typeError) {
        this.row = row;
        this.columnName = columnName;
        this.error = error;
        this.warning = warning;
        this.typeError = typeError;
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

    public Boolean getWarning() {
        return warning;
    }

    public void setWarning(Boolean warning) {
        this.warning = warning;
    }

    public String getTypeError() { return typeError; }

    public void setTypeError(String typeError) { this.typeError = typeError; }

}
