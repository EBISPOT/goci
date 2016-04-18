package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;

import java.util.Collection;

/**
 * Created by emma on 11/04/2016.
 *
 * @author emma
 *         <p>
 *         Entry point for validation system
 */
@Service
public class AssociationValidationService {

    private ValidationServiceBuilder validationBuilder;

    private ValidationChecksBuilder validationChecksBuilder;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationValidationService(ValidationServiceBuilder validationBuilder,
                                        ValidationChecksBuilder validationChecksBuilder) {
        this.validationBuilder = validationBuilder;
        this.validationChecksBuilder = validationChecksBuilder;
    }

    public synchronized Collection<AssociationValidationError> runAssociationValidation(Association association,
                                                                                        String validationLevel) {

        // Determine validation type
        AssociationCheckingService associationCheckingService = validationBuilder.buildValidator(validationLevel);
        return associationCheckingService.runChecks(association, validationChecksBuilder);
    }
}
