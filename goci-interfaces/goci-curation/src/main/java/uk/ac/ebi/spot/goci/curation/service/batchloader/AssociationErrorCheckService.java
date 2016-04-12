package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.BatchUploadError;
import uk.ac.ebi.spot.goci.model.BatchUploadRow;

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
    public Collection<BatchUploadError> runFullChecks(Collection<BatchUploadRow> rows) {

        // Create collection to store all newly created associations
        Collection<BatchUploadError> batchUploadErrors = new ArrayList<>();

        for (BatchUploadRow row : rows) {

            Collection<BatchUploadError> annotationErrors = checkService.runAnnotationChecks(row);
            if (!annotationErrors.isEmpty()) {
                batchUploadErrors.addAll(annotationErrors);
            }

            // Run checks depending on effect type
            String rowEffectType = row.getEffectType();
            if (rowEffectType.equalsIgnoreCase("or")) {
                Collection<BatchUploadError> orErrors = checkService.runOrChecks(row, rowEffectType);
                if (!orErrors.isEmpty()) {
                    batchUploadErrors.addAll(orErrors);
                }
            }

            if (rowEffectType.equalsIgnoreCase("beta")) {
                Collection<BatchUploadError> betaErrors = checkService.runBetaChecks(row, rowEffectType);
                if (!betaErrors.isEmpty()) {
                    batchUploadErrors.addAll(betaErrors);
                }
            }

            if (rowEffectType.equalsIgnoreCase("nr")) {
                Collection<BatchUploadError> noEffectErrors = checkService.runNoEffectErrors(row, rowEffectType);
                if (!noEffectErrors.isEmpty()) {
                    batchUploadErrors.addAll(noEffectErrors);
                }
            }
        }
        return batchUploadErrors;
    }
}