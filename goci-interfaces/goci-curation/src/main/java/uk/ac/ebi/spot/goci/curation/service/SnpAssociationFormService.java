package uk.ac.ebi.spot.goci.curation.service;

import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.model.Association;

/**
 * Created by emma on 26/02/2016.
 *
 * @author emma
 *         <p>
 *         A component that takes a form containing association details and creates an associationobject or vice versa
 */
public interface SnpAssociationFormService {
    /**
     * Create association from values returned in form
     *
     * @return Association, which is normally saved in database
     */
    Association createAssociation(SnpAssociationForm form);

    /**
     * Create form from association
     *
     * @return Form, which is presented via controller to view
     */
    SnpAssociationForm createForm(Association association);
}
