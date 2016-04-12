package uk.ac.ebi.spot.goci.model;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Model class used to represent errors generated after validation of SNP association values
 */
public class AssociationValidationError {

    private Integer row;

    private String columnName;

    private String error;

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
