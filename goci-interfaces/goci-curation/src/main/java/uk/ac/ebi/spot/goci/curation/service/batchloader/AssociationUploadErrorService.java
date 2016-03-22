package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.BatchUploadError;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 */
@Service
public class AssociationUploadErrorService {

    public Collection<BatchUploadError> checkUpload(Collection<BatchUploadRow> rows) {

        // Create collection to store all newly created associations
        Collection<BatchUploadError> batchUploadErrors = new ArrayList<>();

        return batchUploadErrors;
    }
}
