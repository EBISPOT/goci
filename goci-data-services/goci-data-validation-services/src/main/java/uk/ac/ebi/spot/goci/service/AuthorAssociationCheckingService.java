package uk.ac.ebi.spot.goci.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.ValidationError;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 01/04/2016.
 *
 * @author emma
 *         <p>
 *         Full checking service will run all checks
 */
@Service
public class AuthorAssociationCheckingService implements AssociationCheckingService {

    @Override
    public Collection<ValidationError> runChecks(Association association,
                                                 ValidationChecksBuilder validationChecksBuilder) {

        // Create collection to store all newly created associations
        Collection<ValidationError> associationValidationErrors = new ArrayList<>();

        Collection<ValidationError> pvalueErrors = validationChecksBuilder.runPvalueChecks(association);
        if (!pvalueErrors.isEmpty()) {
            associationValidationErrors.addAll(pvalueErrors);
        }

        String effectType = determineIfAssociationIsOrType(association);

        // Run checks depending on effect type
        if (effectType.equalsIgnoreCase("or")) {
            Collection<ValidationError> orErrors =
                    validationChecksBuilder.runOrChecks(association);
            if (!orErrors.isEmpty()) {
                associationValidationErrors.addAll(orErrors);
            }
        }

        if (effectType.equalsIgnoreCase("beta")) {
            Collection<ValidationError> betaErrors =
                    validationChecksBuilder.runAuthorLevelBetaChecks(association);
            if (!betaErrors.isEmpty()) {
                associationValidationErrors.addAll(betaErrors);
            }
        }

        if (effectType.equalsIgnoreCase("none")) {
            Collection<ValidationError> noEffectErrors =
                    validationChecksBuilder.runAuthorLevelNoEffectChecks(association);
            if (!noEffectErrors.isEmpty()) {
                associationValidationErrors.addAll(noEffectErrors);
            }
        }
        return associationValidationErrors;
    }
}