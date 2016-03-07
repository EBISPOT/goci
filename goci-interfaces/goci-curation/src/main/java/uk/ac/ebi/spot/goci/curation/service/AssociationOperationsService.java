package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.model.Association;

/**
 * Created by emma on 03/03/2016.
 *
 * @author emma
 *         <p>
 *         Service class that handles common operations performed on associations
 */
@Service
public class AssociationOperationsService {

    private SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService;
    private SnpInteractionAssociationService snpInteractionAssociationService;

    @Autowired
    public AssociationOperationsService(SingleSnpMultiSnpAssociationService singleSnpMultiSnpAssociationService,
                                        SnpInteractionAssociationService snpInteractionAssociationService) {
        this.singleSnpMultiSnpAssociationService = singleSnpMultiSnpAssociationService;
        this.snpInteractionAssociationService = snpInteractionAssociationService;
    }

    /**
     * Check if association is an OR or BETA type association
     *
     * @param association Association to check
     */
    public Boolean determineIfAssociationIsOrType(Association association) {

        Boolean isOrType = true;
        if (association.getBetaNum() != null) {
            isOrType = false;
        }
        return isOrType;
    }

    /**
     * Generate a the correct form type from association details
     *
     * @param association Association to create form from
     */

    public SnpAssociationForm generateForm(Association association) {

        if (association.getSnpInteraction() != null && association.getSnpInteraction()) {
            return createForm(association, snpInteractionAssociationService);
        }

        else {
            return createForm(association, singleSnpMultiSnpAssociationService);
        }
    }

    /**
     * Create a form from association details
     *
     * @param association Association to create form from
     * @param service     Service to create form
     */
    private SnpAssociationForm createForm(Association association, SnpAssociationFormService service) {
        return service.createForm(association);
    }
}