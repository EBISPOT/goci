package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.BatchUploadError;
import uk.ac.ebi.spot.goci.model.BatchUploadRow;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 30/03/2016.
 *
 * @author emma
 *         <p>
 *         Factory to build various error checks. At present just runs  one set of checks but could could be updated to run "lite" checks just for submitter etc
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
            case "full":
                errors = associationErrorCheckService.runFullChecks(fileRows);
                break;
            default:
                errors = associationErrorCheckService.runFullChecks(fileRows);
                break;
        }
        return errors;
    }
}
