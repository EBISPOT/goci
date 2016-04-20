package uk.ac.ebi.spot.goci.model;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         A more specialised class used to represent errors generated after validation of an upload spreadsheet
 *         containing association values
 */
public class AssociationValidationError extends ValidationError {

    private Integer row;

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }
}
