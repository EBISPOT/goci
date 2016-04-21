package uk.ac.ebi.spot.goci.service;

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
 *         Check a row for missing values or syntax values
 */
@Service
public class RowCheckingService {

    public Collection<ValidationError> runChecks(AssociationUploadRow row) {
        // Create collection to store all newly created associations
        Collection<ValidationError> errors = new ArrayList<>();
        return errors;
    }
}
