package uk.ac.ebi.spot.goci.model;

/**
 * Created by emma on 20/04/2016.
 *
 * @author emma
 *         <p>
 *         Class used to represent a newly created row and any errors attahced
 */
public class RowValidationSummary extends ValidationSummary {

    private AssociationUploadRow row;

    public AssociationUploadRow getRow() {
        return row;
    }

    public void setRow(AssociationUploadRow row) {
        this.row = row;
    }
}
