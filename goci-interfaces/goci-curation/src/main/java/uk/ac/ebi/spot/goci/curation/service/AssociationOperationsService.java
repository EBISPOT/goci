package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
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
}