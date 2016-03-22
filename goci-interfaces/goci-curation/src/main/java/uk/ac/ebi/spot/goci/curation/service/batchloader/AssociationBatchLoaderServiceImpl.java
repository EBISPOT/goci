package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.BatchUploadError;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Service
public class AssociationBatchLoaderServiceImpl implements AssociationBatchLoaderService {

    private AssociationSheetProcessor associationSheetProcessor;
    private AssociationUploadErrorService associationUploadErrorService;
    private RowProcessor rowProcessor;
    private AssociationRepository associationRepository;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationBatchLoaderServiceImpl(AssociationSheetProcessor associationSheetProcessor,
                                             AssociationUploadErrorService associationUploadErrorService,
                                             RowProcessor rowProcessor,
                                             AssociationRepository associationRepository) {
        this.associationSheetProcessor = associationSheetProcessor;
        this.associationUploadErrorService = associationUploadErrorService;
        this.rowProcessor = rowProcessor;
        this.associationRepository = associationRepository;
    }

    /**
     * Process uploaded file
     *
     * @return errors, any errors encountered
     */
    @Override public Collection<BatchUploadError> processFile(String fileName, Study study)
            throws IOException, InvalidFormatException {

        // Open file
        OPCPackage pkg = OPCPackage.open(fileName);
        XSSFWorkbook current = new XSSFWorkbook(pkg);
        XSSFSheet sheet = current.getSheetAt(0);

        // Read file rows
        Collection<BatchUploadRow> rows = associationSheetProcessor.readSheetRows(sheet);

        // Check each row for errors
        Collection<BatchUploadError> errors = checkUploadForErrors(rows);

        // Create associations
        Collection<Association> associations = processFileRows(rows);

        // Save associations

        if (errors.isEmpty()) {
            if (!associations.isEmpty()) {

                saveAssociations(associations, study);

                // Delete our file once associations are saved
                File fileToDelete = new File(fileName);
                fileToDelete.deleteOnExit();

            }
            else {
                getLog().error("No associations created for: " + fileName);
            }
        }
        else {
            getLog().error("Errors found in file: " + fileName);
        }

        pkg.close();
        return errors;
    }

    @Override public Collection<Association> processFileRows(Collection<BatchUploadRow> batchUploadRows) {
        return rowProcessor.createAssociationsFromUploadRows(batchUploadRows);
    }

    @Override public Collection<BatchUploadError> checkUploadForErrors(Collection<BatchUploadRow> sheet) {
        Collection<BatchUploadError> errors = new ArrayList<>();
        errors = associationUploadErrorService.checkUpload(sheet);
        return errors;
    }

    @Override public void saveAssociations(Collection<Association> associations, Study study) {
        Collection<Association> associationsToMap = new ArrayList<>();
        for (Association association : associations) {

            // Set the study ID for our association
            association.setStudy(study);

            // Save our association information
            association.setLastUpdateDate(new Date());
            associationRepository.save(association);
        }
    }
}