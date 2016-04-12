package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 01/04/2016.
 *
 * @author emma
 */
@Service
public class FullAssociationCheckingService implements AssociationCheckingService {

    private CheckingService checkingService;

    @Autowired
    public void setCheckingService(CheckingService checkingService) {
        this.checkingService = checkingService;
    }

    @Override public Collection<AssociationValidationError> runChecks(Association association, String effectType) {

        // Create collection to store all newly created associations
        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        Collection<AssociationValidationError> annotationErrors = checkingService.runAnnotationChecks(association);
        if (!annotationErrors.isEmpty()) {
            associationValidationErrors.addAll(annotationErrors);
        }

        // Run checks depending on effect type
        if (effectType.equalsIgnoreCase("or")) {
            Collection<AssociationValidationError> orErrors = checkingService.runOrChecks(association, effectType);
            if (!orErrors.isEmpty()) {
                associationValidationErrors.addAll(orErrors);
            }
        }

        if (effectType.equalsIgnoreCase("beta")) {
            Collection<AssociationValidationError> betaErrors = checkingService.runBetaChecks(association, effectType);
            if (!betaErrors.isEmpty()) {
                associationValidationErrors.addAll(betaErrors);
            }
        }

        if (effectType.equalsIgnoreCase("nr")) {
            Collection<AssociationValidationError> noEffectErrors =
                    checkingService.runNoEffectErrors(association, effectType);
            if (!noEffectErrors.isEmpty()) {
                associationValidationErrors.addAll(noEffectErrors);
            }
        }
        return associationValidationErrors;
    }
}