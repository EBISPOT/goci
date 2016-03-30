package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadError;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 30/03/2016.
 *
 * @author emma
 *         <p>
 *         Factory to build various error checks
 */
@Service
public class AssociationErrorCheckFactory {

    private AssociationErrorCheckService associationErrorCheckService;

    @Autowired
    public AssociationErrorCheckFactory(AssociationErrorCheckService associationErrorCheckService) {
        this.associationErrorCheckService = associationErrorCheckService;
    }

    public Collection<BatchUploadError> runChecks(String checkLevel, Collection<BatchUploadRow> fileRows) {

        Collection<BatchUploadError> errors = new ArrayList<>();

        switch (checkLevel) {
            case "all":
                errors = associationErrorCheckService.runFullChecks(fileRows);
                break;
            default:
                errors = associationErrorCheckService.runFullChecks(fileRows);
                break;
        }
        return errors;
    }
}
