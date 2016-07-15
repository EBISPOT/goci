package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;

import java.util.List;

/**
 * Created by emma on 15/07/2016.
 *
 * @author emma
 *         <p>
 *         Builder for SnpAssociationInteractionForm
 */
public class SnpAssociationInteractionFormBuilder {

    private SnpAssociationInteractionForm snpAssociationInteractionForm = new SnpAssociationInteractionForm();

    public SnpAssociationInteractionFormBuilder setSnpFormColumns(List<SnpFormColumn> snpFormColumns) {
        snpAssociationInteractionForm.setSnpFormColumns(snpFormColumns);
        return this;
    }

    public SnpAssociationInteractionFormBuilder setNumOfInteractions(Integer numOfInteractions) {
        snpAssociationInteractionForm.setNumOfInteractions(numOfInteractions);
        return this;
    }

    public SnpAssociationInteractionForm build() {
        return snpAssociationInteractionForm;
    }
}