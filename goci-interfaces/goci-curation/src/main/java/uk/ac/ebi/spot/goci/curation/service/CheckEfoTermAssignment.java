package uk.ac.ebi.spot.goci.curation.service;


import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;

/**
 * Created by emma on 17/02/2016.
 *
 * @author emma
 *         <p>
 *         Check EFO terms are assigned to either study or a studies associatiions
 */
@Service
public class CheckEfoTermAssignment {

    /**
     * Check study to ensure it has EFO trait assigned
     *
     * @param allStudyAssociations All associations found for a study
     */
    public Boolean checkAssociationsEfoAssignment(Collection<Association> allStudyAssociations) {
        Boolean efoTermsAssigned = true;

        for (Association association : allStudyAssociations) {

            // If we have one that is not checked set value
            Collection<EfoTrait> associationTraits = association.getEfoTraits();

            if (associationTraits == null) {
                efoTermsAssigned = false;
                break;
            }
            else {
                if (associationTraits.isEmpty()) {
                    efoTermsAssigned = false;
                    break;
                }
            }
        }
        return efoTermsAssigned;
    }

    /**
     * Check association to ensure it has EFO trait assigned
     *
     * @param association
     */
    public Boolean checkAssociationEfoAssignment(Association association) {
        Boolean efoTermsAssigned = true;

        // If we have one that is not checked set value
        Collection<EfoTrait> associationTraits = association.getEfoTraits();

        if (associationTraits == null) {
            efoTermsAssigned = false;
        }
        else {
            if (associationTraits.isEmpty()) {
                efoTermsAssigned = false;
            }
        }

        return efoTermsAssigned;
    }

    /**
     * Check study to ensure it has EFO trait assigned
     *
     * @param study
     */
    public Boolean checkStudyEfoAssignment(Study study) {
        Boolean efoTermsAssigned = true;

        // If we have one that is not checked set value
        Collection<EfoTrait> studyEfoTraits = study.getEfoTraits();

        if (studyEfoTraits == null) {
            efoTermsAssigned = false;
        }
        else {
            if (studyEfoTraits.isEmpty()) {
                efoTermsAssigned = false;
            }
        }

        return efoTermsAssigned;
    }

}
