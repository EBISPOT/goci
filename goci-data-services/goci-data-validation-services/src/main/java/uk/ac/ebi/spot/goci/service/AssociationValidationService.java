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
 */
@Service
public class AssociationValidationService {

    private ValidationBuilder validationBuilder;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationValidationService(ValidationBuilder validationBuilder) {
        this.validationBuilder = validationBuilder;
    }

    public Collection<AssociationValidationError> runAssociationValidation(Association association,
                                                                           String validationLevel,
                                                                           String effectType) {

        // Determine validation type
        AssociationCheckingService associationCheckingService = validationBuilder.buildValidator(validationLevel);
        return associationCheckingService.runChecks(association, effectType);
    }
}
