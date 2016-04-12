package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Error checking of uploaded spreadsheet
 */
@Service
public class AssociationErrorCheckService {

    private CheckService checkService;

    @Autowired
    public AssociationErrorCheckService(CheckService checkService) {
        this.checkService = checkService;
    }

    /**
     * Check file rows for any errors
     *
     * @return errors, any errors encountered
     */
    public Collection<AssociationValidationError> runFullChecks(Collection<AssociationUploadRow> rows) {

        // Create collection to store all newly created associations
        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        for (AssociationUploadRow row : rows) {

            Collection<AssociationValidationError> annotationErrors = checkService.runAnnotationChecks(row);
            if (!annotationErrors.isEmpty()) {
                associationValidationErrors.addAll(annotationErrors);
            }

            // Run checks depending on effect type
            String rowEffectType = row.getEffectType();
            if (rowEffectType.equalsIgnoreCase("or")) {
                Collection<AssociationValidationError> orErrors = checkService.runOrChecks(row, rowEffectType);
                if (!orErrors.isEmpty()) {
                    associationValidationErrors.addAll(orErrors);
                }
            }

            if (rowEffectType.equalsIgnoreCase("beta")) {
                Collection<AssociationValidationError> betaErrors = checkService.runBetaChecks(row, rowEffectType);
                if (!betaErrors.isEmpty()) {
                    associationValidationErrors.addAll(betaErrors);
                }
            }

            if (rowEffectType.equalsIgnoreCase("nr")) {
                Collection<AssociationValidationError> noEffectErrors = checkService.runNoEffectErrors(row, rowEffectType);
                if (!noEffectErrors.isEmpty()) {
                    associationValidationErrors.addAll(noEffectErrors);
                }
            }
        }
        return associationValidationErrors;
    }
}