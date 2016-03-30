package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadError;
import uk.ac.ebi.spot.goci.curation.model.batchloader.BatchUploadRow;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Implementation of methods used to process associaton batch upload file
 */
@Service
public class AssociationBatchLoaderServiceImpl implements AssociationBatchLoaderService {

    private SheetProcessor sheetProcessor;
    private AssociationErrorCheckFactory associationErrorCheckFactory;
    private RowProcessor rowProcessor;
    private AssociationRepository associationRepository;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationBatchLoaderServiceImpl(SheetProcessor sheetProcessor,
                                             AssociationErrorCheckFactory associationErrorCheckFactory,
                                             RowProcessor rowProcessor,
                                             AssociationRepository associationRepository) {
        this.sheetProcessor = sheetProcessor;
        this.associationErrorCheckFactory = associationErrorCheckFactory;
        this.rowProcessor = rowProcessor;
        this.associationRepository = associationRepository;
    }

    /**
     * Process uploaded file
     *
     * @return errors, any errors encountered
     */
    @Override public Collection<BatchUploadRow> processFile(String fileName, Study study)
            throws IOException, InvalidFormatException {

        // Open file
        OPCPackage pkg = OPCPackage.open(fileName);
        XSSFWorkbook current = new XSSFWorkbook(pkg);
        XSSFSheet sheet = current.getSheetAt(0);

        if (sheet.getFirstRowNum() == sheet.getLastRowNum()) {
            getLog().error(fileName + " is empty");
            throw new IOException();
        }
        else {
            // Read file rows
            Collection<BatchUploadRow> rows = sheetProcessor.readSheetRows(sheet);
            pkg.close();
            return rows;
        }
    }

    /**
     * Process rows in uploaded file
     *
     * @param fileRows collection of file rows
     * @return collection of associations to be saved
     */
    @Override public Collection<Association> processFileRows(Collection<BatchUploadRow> fileRows) {
        return rowProcessor.createAssociationsFromUploadRows(fileRows);
    }

    /**
     * Check rows for errors
     *
     * @param fileRows collection of file rows
     * @return collection of errors
     */
    @Override public Collection<BatchUploadError> checkUploadForErrors(Collection<BatchUploadRow> fileRows) {
        return associationErrorCheckFactory.runChecks("all", fileRows);
    }

    /**
     * Save associations created from file
     *
     * @param associations collection of associations
     * @param study        study to assign associations to
     */
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

    /**
     * Delete uploaded file
     */
    @Override public void deleteFile(String fileName) {
        // Delete our file once associations are saved
        File fileToDelete = new File(fileName);
        fileToDelete.delete();
    }
}