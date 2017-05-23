package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationReport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 24/11/2015.
 */
@Service
public class AssociationMappingErrorService {

    public AssociationMappingErrorService() {
    }

    /**
     * Create object, from Association Report, that will be returned to view
     *
     * @param associationReport Association Report object containing mapping errors
     */
    public Map<String, String> createAssociationErrorMap(AssociationReport associationReport) {

        Map<String, String> associationErrorMap = new HashMap<>();

        //Create map of errors
        if (associationReport != null) {
            if (associationReport.getSnpError() != null && !associationReport.getSnpError().isEmpty()) {
                associationErrorMap.put("SNP Error", associationReport.getSnpError());
            }

            if (associationReport.getSnpGeneOnDiffChr() != null &&
                    !associationReport.getSnpGeneOnDiffChr().isEmpty()) {
                associationErrorMap.put("Snp Gene On Diff Chr", associationReport.getSnpGeneOnDiffChr());
            }

            if (associationReport.getNoGeneForSymbol() != null &&
                    !associationReport.getNoGeneForSymbol().isEmpty()) {
                associationErrorMap.put("No Gene For Symbol", associationReport.getNoGeneForSymbol());
            }

            if (associationReport.getRestServiceError() != null &&
                    !associationReport.getRestServiceError().isEmpty()) {
                associationErrorMap.put("Rest Service Error", associationReport.getRestServiceError());
            }

            if (associationReport.getSuspectVariationError() != null &&
                    !associationReport.getSuspectVariationError().isEmpty()) {
                associationErrorMap.put("Suspect variation", associationReport.getSuspectVariationError());
            }

            if (associationReport.getGeneError() != null &&
                    !associationReport.getGeneError().isEmpty()) {
                associationErrorMap.put("Gene Error", associationReport.getGeneError());
            }
        }

        return associationErrorMap;
    }

}
