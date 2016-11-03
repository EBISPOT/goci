package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.ValidationError;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 21/04/2016.
 *
 * @author emma
 *         <p>
 *         Check a row for missing values or syntax errors
 */
@Service
public class RowCheckingService {

    private RowChecksBuilder rowChecksBuilder;

    @Autowired
    public RowCheckingService(RowChecksBuilder rowChecksBuilder) {
        this.rowChecksBuilder = rowChecksBuilder;
    }

    public Collection<ValidationError> runChecks(AssociationUploadRow row) {
        // Create collection to store all newly created associations
        Collection<ValidationError> errors = new ArrayList<>();

        // Check for missing essential values like SNP or risk allele
        Collection<ValidationError> emptyValuesErrors = rowChecksBuilder.runEmptyValueChecks(row);
        if (!emptyValuesErrors.isEmpty()) {
            errors.addAll(emptyValuesErrors);
        }
        else {
            // Check that SNP and risk allele use the correct delimiter
            // i.e. x = SNP interaction, ; = multi-snp haplotype
            Collection<ValidationError> synthaxErrors = rowChecksBuilder.runSynthaxChecks(row);
            if (!synthaxErrors.isEmpty()) {
                errors.addAll(synthaxErrors);
            }
        }

        //Add the XLS convertion errors to the general error list.
        if (!row.getListErrorCellType().isEmpty()){
            errors.addAll(row.getListErrorCellType());
        }

        return errors;
    }
}
