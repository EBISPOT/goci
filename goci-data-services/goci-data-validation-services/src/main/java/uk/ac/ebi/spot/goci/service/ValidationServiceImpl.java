package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.ValidationError;

import java.util.Collection;

/**
 * Created by emma on 11/04/2016.
 *
 * @author emma
 *         <p>
 *         Entry point for validation system
 */
@Service
public class ValidationServiceImpl implements ValidationService{

    @Autowired
    private ValidationServiceBuilder validationBuilder;

    @Autowired
    private ValidationChecksBuilder validationChecksBuilder;

    @Autowired
    private RowCheckingService rowCheckingService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override public Collection<ValidationError> runAssociationValidation(Association association,
                                                                             String validationLevel) {

        // Determine validation type
        AssociationCheckingService associationCheckingService = validationBuilder.buildValidator(validationLevel);
        return associationCheckingService.runChecks(association, validationChecksBuilder);
    }

    @Override public Collection<ValidationError> runRowValidation(AssociationUploadRow row) {
        return rowCheckingService.runChecks(row);
    }
}
