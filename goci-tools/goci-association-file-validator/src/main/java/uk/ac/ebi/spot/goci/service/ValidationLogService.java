package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.RowValidationSummary;
import uk.ac.ebi.spot.goci.model.ValidationError;
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String now = dateFormat.format(new Date());
        String filePath = checkedFile.getParent();

        // If there is no file path just use current working dir
        if (filePath == null) {
            filePath = Paths.get(".").toAbsolutePath().normalize().toString();
        }

        String logName = filePath
                .concat(File.separator)
                .concat(("Validation_results_").concat(now).concat(".txt"));

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

        bw.write("Validation results:" + "\n\n");

        for (RowValidationSummary rowValidationSummary : rowValidationSummaries) {

            if (!rowValidationSummary.getErrors().isEmpty()) {
                for (ValidationError rowError : rowValidationSummary.getErrors()) {
                    bw.write("Row number: " + rowValidationSummary.getRow() + "\t" +
                                     rowError.getField() +
                                     "\t" + rowError.getError() + "\n");
                }
            }
            else {
                bw.write("No syntax errors found in row");
            }
        }

        for (AssociationSummary associationSummary : associationSummaries) {
            if (!associationSummary.getErrors().isEmpty()) {

                bw.write("\n\n");
                for (ValidationError associationError : associationSummary.getErrors()) {
                    bw.write("Row number: " + associationSummary.getRowNumber() + "\t" +
                                     associationError.getField() +
                                     "\t" + associationError.getError() + "\n");
                }
            }
            else {
                bw.write("No error found in association values");
            }
        }

        bw.close();
    }
}