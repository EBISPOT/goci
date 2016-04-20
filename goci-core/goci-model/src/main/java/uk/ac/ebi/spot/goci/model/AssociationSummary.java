package uk.ac.ebi.spot.goci.model;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Class used to represent a newly created association and any errors attahced
 */
public class AssociationSummary extends ValidationSummary {

    private Association association;

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }
}
