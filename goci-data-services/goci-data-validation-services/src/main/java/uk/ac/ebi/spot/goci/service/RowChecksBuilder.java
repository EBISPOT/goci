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
        errors.add(errorCreationService.checkSnpValueIsPresent(row));
        errors.add(errorCreationService.checkStrongestAlleleValueIsPresent(row));
        return ErrorProcessingService.checkForValidErrors(errors);
    }
}
