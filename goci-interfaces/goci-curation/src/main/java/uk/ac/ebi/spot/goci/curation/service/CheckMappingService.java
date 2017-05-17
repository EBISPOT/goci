package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;

import java.util.Collection;

/**
 * Created by Cinzia on 2/5/2017
 *
 * @author Cinzia
 *         <p>
 *         Check Mapping are assigned to the associations.
 */

@Service
public class CheckMappingService {

    public Boolean checkAssociationsMappingAssignment(Collection<Association> allStudyAssociations) {
        Boolean mappingAssigned = true;

        for (Association association : allStudyAssociations) {

            mappingAssigned = checkAssociationMappingAssignment(association);
            if (!mappingAssigned) {
                break;
            }
        }

        return mappingAssigned;
    }


    public Boolean checkAssociationMappingAssignment(Association association) {
        Boolean mappingAssigned = true;

        AssociationReport associationReport = association.getAssociationReport();

        if (associationReport == null) {
            mappingAssigned = false;
        }

        return mappingAssigned;
    }

}

