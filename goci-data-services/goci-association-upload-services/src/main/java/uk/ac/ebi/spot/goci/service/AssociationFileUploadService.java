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
import uk.ac.ebi.spot.goci.model.RowValidationSummary;
import uk.ac.ebi.spot.goci.model.ValidationError;
import uk.ac.ebi.spot.goci.model.ValidationSummary;

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

    private RowValidationService rowValidationService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationFileUploadService(UploadSheetProcessor uploadSheetProcessor,
                                        AssociationRowProcessor associationRowProcessor,
                                        AssociationValidationService associationValidationService,
                                        SheetCreationService sheetCreationService,
                                        RowValidationService rowValidationService) {
        this.uploadSheetProcessor = uploadSheetProcessor;
        this.associationRowProcessor = associationRowProcessor;
        this.associationValidationService = associationValidationService;
        this.sheetCreationService = sheetCreationService;
        this.rowValidationService = rowValidationService;
    }

    /**
     * Process uploaded file and return a list of errors
     *
     * @param file XLSX file supplied by user
     */
    public synchronized ValidationSummary processAssociationFile(File file, String validationLevel)
            throws FileNotFoundException {

        ValidationSummary validationSummary = new ValidationSummary();
        Collection<RowValidationSummary> rowValidationSummaries = new ArrayList<>();
        Collection<AssociationSummary> associationSummaries = new ArrayList<>();
        Collection<AssociationUploadRow> fileRows = new ArrayList<>();

        if (file.exists()) {
            // Create sheet
            XSSFSheet sheet = null;
            try {
                // Create a sheet for reading
                sheet = sheetCreationService.createSheet(file.getAbsolutePath());

                // Process file into a generic row object
                fileRows = uploadSheetProcessor.readSheetRows(sheet);
            }
            catch (InvalidFormatException | IOException e) {
                getLog().error("File: " + file.getName() + " cannot be processed", e);
            }
        }
        else {
            getLog().error("File: " + file.getName() + " cannot be found");
            throw new FileNotFoundException("File does not exist");
        }

        // Error check each row
        if (!fileRows.isEmpty()) {
            // Check for missing values and syntax errors that would prevent code creating an association
            for (AssociationUploadRow row : fileRows) {
                getLog().debug("Syntax checking row: " + row.getRowNumber() + " of file, " + file.getAbsolutePath());
                rowValidationSummaries.add(createRowValidationSummary(row));
            }

            if (rowValidationSummaries.isEmpty()) {
                //Proceed to carry out full checks of values
                for (AssociationUploadRow row : fileRows) {
                    associationSummaries.add(createAssociationSummary(row, validationLevel));
                }
            }
        }
        else {
            getLog().error("Parsing file failed");
        }

        validationSummary.setAssociationSummaries(associationSummaries);
        validationSummary.setRowValidationSummaries(rowValidationSummaries);
        return validationSummary;
    }

    /**
     * Return a list of syntax errors. These error checks will look for things that would
     * prevent creation of an association which could then be carried forward to full validation
     *
     * @param row Row to validate
     */
    private RowValidationSummary createRowValidationSummary(AssociationUploadRow row) {
        Collection<ValidationError> errors = rowValidationService.runRowValidation(row);
        RowValidationSummary rowValidationSummary = new RowValidationSummary();
        rowValidationSummary.setRow(row);
        rowValidationSummary.setErrors(errors);
        return rowValidationSummary;
    }

    /**
     * Process uploaded file, create an association and return a list of its errors
     *
     * @param row             Row to validate and convert into an association
     * @param validationLevel level of validation to run
     */
    private AssociationSummary createAssociationSummary(AssociationUploadRow row, String validationLevel) {
        Association association = associationRowProcessor.createAssociationFromUploadRow(row);
        Collection<ValidationError> errors =
                associationValidationService.runAssociationValidation(association, validationLevel);
        AssociationSummary associationSummary = new AssociationSummary();
        associationSummary.setRowNumber(row.getRowNumber());
        associationSummary.setAssociation(association);
        associationSummary.setErrors(errors);
        return associationSummary;
    }
}