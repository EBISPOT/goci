package uk.ac.ebi.spot.goci.curation.model;

/**
 * Created by emma on 29/05/2015.
 */
public class AssociationFormErrorView {

    private String snpError;

    // Constructor
    public AssociationFormErrorView() {

    }

    public AssociationFormErrorView(String snpError) {
        this.snpError = snpError;
    }

    public String getSnpError() {
        return snpError;
    }

    public void setSnpError(String snpError) {
        this.snpError = snpError;
    }
}
