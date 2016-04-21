package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.ValidationError;

import java.util.Collection;

/**
 * Created by emma on 21/04/2016.
 *
 * @author emma
 *         <p>
 *         This service performs high level row checks to ensure the supplied data can be used to create an association
 */
@Service
public class RowValidationService {

    private RowCheckingService rowCheckingService;

    @Autowired
    public RowValidationService(RowCheckingService rowCheckingService) {
        this.rowCheckingService = rowCheckingService;
    }

    public synchronized Collection<ValidationError> runRowValidation(AssociationUploadRow row) {
        return rowCheckingService.runChecks(row);
    }
}
