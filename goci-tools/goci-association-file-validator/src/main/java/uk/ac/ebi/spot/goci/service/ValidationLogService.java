package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.RowValidationSummary;
import uk.ac.ebi.spot.goci.model.ValidationSummary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 18/04/2016.
 *
 * @author emma
 *         <p>
 *         Service to create a log file contain errors generated after checking an uplaod spreadsheet containing
 *         association information
 */
@Service
public class ValidationLogService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void processErrors(File checkedFile, ValidationSummary validationSummary) {

        Collection<AssociationSummary> associationSummaries = validationSummary.getAssociationSummaries();
        Collection<RowValidationSummary> rowValidationSummaries = validationSummary.getRowValidationSummaries();

        /// Create the log file
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'-T'HH-mm-ss");
        String now = dateFormat.format(new Date());
        String filePath = checkedFile.getParent();

        // If there is no file path just use current working dir
        if (filePath == null) {
            filePath = Paths.get(".").toAbsolutePath().normalize().toString();
        }

        String logName = filePath
                .concat(File.separator)
                .concat(("Validation_results_").concat(now).concat(".txt"));

        System.out.println("Validation log written to " + logName);
        getLog().info("Validation log written to " + logName);

        // Write errors to file
        boolean fileCreationSuccess = false;
        File file = new File(logName);

        try {
            if (!file.exists()) {
                fileCreationSuccess = file.createNewFile();
            }
        }
        catch (IOException e) {
            getLog().error("Creating validation log failed");
            e.printStackTrace();
        }

        if (fileCreationSuccess) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(file.getAbsoluteFile());
            }
            catch (IOException e) {
                getLog().error("Accessing validation log failed");
                e.printStackTrace();
            }
            BufferedWriter bw = new BufferedWriter(fw);
            try {
                writeErrorToFile(bw, associationSummaries, rowValidationSummaries);
            }
            catch (IOException e) {
                getLog().error("Writing to validation log failed");
                e.printStackTrace();
            }
        }
        else {
            getLog().error("Creating validation log failed");
        }
    }

    /**
     * Write errors to log
     *
     * @param bw                     buffer to write to
     * @param associationSummaries   summary of validation run of associations
     * @param rowValidationSummaries summary of validation run of rows
     */
    private void writeErrorToFile(BufferedWriter bw,
                                  Collection<AssociationSummary> associationSummaries,
                                  Collection<RowValidationSummary> rowValidationSummaries) throws IOException {

        bw.write("Validation results:".concat("\n\n"));
        bw.write("Row".concat("\t").concat("Field").concat("\t").concat("Error Message").concat("\n"));

        // Check if we have any errors
        long rowErrorCount = rowValidationSummaries.parallelStream()
                .filter(rowValidationSummary -> !rowValidationSummary.getErrors().isEmpty())
                .count();

        long associationErrorCount = associationSummaries.parallelStream()
                .filter(associationSummary -> !associationSummary.getErrors().isEmpty())
                .count();

        if (rowErrorCount > 0) {
            rowValidationSummaries.forEach(rowValidationSummary -> {
                rowValidationSummary.getErrors().forEach(validationError -> {
                    try {
                        bw.write(String.valueOf(rowValidationSummary.getRow().getRowNumber())
                                         .concat("\t")
                                         .concat(validationError.getField())
                                         .concat("\t")
                                         .concat(validationError.getError())
                                         .concat("\n"));
                    }
                    catch (IOException e) {
                        getLog().error("Writing errors to file failed");
                        e.printStackTrace();
                    }
                });
            });
        }
        else {
            getLog().info("No syntax errors found in row. Proceeding to full checking");
        }

        // Only report association errors if there are no row error
        if (associationErrorCount > 0 && rowErrorCount == 0) {
            bw.write("\n\n");
            associationSummaries.forEach(associationSummary -> {
                associationSummary.getErrors().forEach(validationError -> {
                    try {
                        bw.write(String.valueOf(associationSummary.getRowNumber())
                                         .concat("\t")
                                         .concat(validationError.getField())
                                         .concat("\t")
                                         .concat(validationError.getError())
                                         .concat("\n"));
                    }
                    catch (IOException e) {
                        getLog().error("Writing errors to file failed");
                        e.printStackTrace();
                    }
                });
            });
        }
        else {
            bw.write("No error found in association values".concat("\n"));
        }
        bw.close();
    }
}