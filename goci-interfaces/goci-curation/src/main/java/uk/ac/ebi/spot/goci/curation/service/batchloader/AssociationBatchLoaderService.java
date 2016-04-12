package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Upload, error check and create persistance obejects that can be saved in DB from a batch of SNPs from a .xlsx
 *         spreadsheet. Note that the spreadsheet must be of .xlsx format!
 *         <p>
 *         Created from code originally written by Dani. Adapted to fit with new curation system.
 */
public interface AssociationBatchLoaderService {

    Collection<AssociationUploadRow> processFile(String fileName, Study study) throws InvalidFormatException, IOException;

    Collection<Association> processFileRows(Collection<AssociationUploadRow> associationUploadRows);

    Collection<AssociationValidationError> checkUploadForErrors(Collection<AssociationUploadRow> associationUploadRows);

    void saveAssociations(Collection<Association> associations, Study study);

    void deleteFile(String fileName);
}
