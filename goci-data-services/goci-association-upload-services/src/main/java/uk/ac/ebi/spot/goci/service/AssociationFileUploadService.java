package uk.ac.ebi.spot.goci.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 01/04/2016.
 *
 * @author emma
 *         <p>
 *         This service class acts an an entry point for processing an XLSX file that contains GWAS association data
 */
@Service
public class AssociationFileUploadService {

    private UploadSheetProcessor uploadSheetProcessor;

    private AssociationRowProcessor associationRowProcessor;

    private AssociationValidationService associationValidationService;

    private SheetCreationService sheetCreationService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationFileUploadService(UploadSheetProcessor uploadSheetProcessor,
                                        AssociationRowProcessor associationRowProcessor,
                                        AssociationValidationService associationValidationService,
                                        SheetCreationService sheetCreationService) {
        this.uploadSheetProcessor = uploadSheetProcessor;
        this.associationRowProcessor = associationRowProcessor;
        this.associationValidationService = associationValidationService;
        this.sheetCreationService = sheetCreationService;
    }

    /**
     * Process uploaded file and return a list of its errors
     *
     * @param file XLSX file supplied by user
     */
    public Collection<AssociationSummary> processAssociationFile(File file, String validationLevel) throws FileNotFoundException {

        Collection<AssociationSummary> associationSummaries = new ArrayList<>();
        if (file.exists()) {
            // Create sheet
            XSSFSheet sheet = null;
            try {
                // Create a sheet for reading
                sheet = sheetCreationService.createSheet(file.getAbsolutePath());

                // Process file
                Collection<AssociationUploadRow> fileRows = uploadSheetProcessor.readSheetRows(sheet);

                // Error check each row
                for (AssociationUploadRow row : fileRows) {
                    getLog().debug("Error checking row: " + row.getRowNumber() + " of file, " + file.getAbsolutePath());
                    Association association = associationRowProcessor.createAssociationFromUploadRow(row);
                    Collection<AssociationValidationError> errors =
                            associationValidationService.runAssociationValidation(association, validationLevel);

                    AssociationSummary associationSummary = new AssociationSummary();
                    associationSummary.setAssociation(association);
                    associationSummary.setAssociationValidationErrors(errors);
                    associationSummaries.add(associationSummary);
                }
            }
            catch (InvalidFormatException | IOException e) {
                getLog().error("File: " + file.getName() + " cannot be processed", e);
            }
        }
        else {
            getLog().error("File: " + file.getName() + " cannot be found");
            throw new FileNotFoundException("File does not exist");
        }
        return associationSummaries;
    }
}