package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.ValidationError;
import uk.ac.ebi.spot.goci.utils.ErrorProcessingService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 18/04/2016.
 *
 * @author emma
 *         <p>
 *         Class that runs various combinations of error checks
 */
@Service
public class RowChecksBuilder {

    private ErrorCreationService errorCreationService;

    @Autowired
    public RowChecksBuilder(ErrorCreationService errorCreationService) {
        this.errorCreationService = errorCreationService;
    }

    /**
     * Run checks for empty values on a row
     *
     * @param row Row to be checked
     */
    public Collection<ValidationError> runEmptyValueChecks(AssociationUploadRow row) {

        Collection<ValidationError> errors = new ArrayList<>();
        errors.add(errorCreationService.checkSnpValueIsPresent(row.getSnp()));
        errors.add(errorCreationService.checkStrongestAlleleValueIsPresent(row.getStrongestAllele()));
        return ErrorProcessingService.checkForValidErrors(errors);
    }

    /**
     * Run checks for synthax errors depending on association type
     *
     * @param row Row to be checked
     */
    public Collection<ValidationError> runSynthaxChecks(AssociationUploadRow row) {

        Collection<ValidationError> errors = new ArrayList<>();

        if (row.getMultiSnpHaplotype().equalsIgnoreCase("Y")) {
            errors.add(errorCreationService.checkSnpSynthax(row.getSnp(), ";"));
            errors.add(errorCreationService.checkRiskAlleleSynthax(row.getStrongestAllele(), ";"));
        }

        if (row.getSnpInteraction().equalsIgnoreCase("Y")) {
            errors.add(errorCreationService.checkSnpSynthax(row.getSnp(), "x"));
            errors.add(errorCreationService.checkRiskAlleleSynthax(row.getStrongestAllele(), "x"));
            if (row.getAuthorReportedGene() != null && !row.getAuthorReportedGene().isEmpty()) {
                errors.add(errorCreationService.checkGeneSynthax(row.getAuthorReportedGene(), "x"));
            }
        }
        return ErrorProcessingService.checkForValidErrors(errors);
    }
}