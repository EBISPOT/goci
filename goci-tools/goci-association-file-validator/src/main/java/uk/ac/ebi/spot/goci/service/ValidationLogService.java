package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationSummary;
import uk.ac.ebi.spot.goci.model.AssociationValidationError;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public void createLog(File checkedFile, Collection<AssociationSummary> associationSummaries) {

        // Get all errors
        Collection<AssociationValidationError> errors = getAllErrors(associationSummaries);

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
        try {
            File file = new File(logName);

            boolean fileCreationSuccess = false;
            if (!file.exists()) {
                fileCreationSuccess = file.createNewFile();
            }

            if (fileCreationSuccess) {
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("Validation results for file: " + checkedFile.getName() + "\n\n");
                if (!errors.isEmpty()) {

                    for (AssociationValidationError associationValidationError : errors) {
                        bw.write(associationValidationError.getRow() + "\t" +
                                         associationValidationError.getColumnName() +
                                         "\t" + associationValidationError.getError() + "\n");
                    }
                }
                else {
                    bw.write("No errors found" + "\n");
                }
                bw.close();
            }
            else {
                getLog().error("Creating validation log failed");
            }
        }
        catch (IOException e) {
            getLog().error("Creating validation log failed");
            e.printStackTrace();
        }
    }

    /**
     * Get all errors which can then be added to a log
     *
     * @param associationSummaries collection of association summaries which contain an association and its associated
     *                             collection of errors
     */
    private Collection<AssociationValidationError> getAllErrors(Collection<AssociationSummary> associationSummaries) {

        Collection<AssociationValidationError> associationValidationErrors = new ArrayList<>();

        for (AssociationSummary associationSummary : associationSummaries) {
            associationValidationErrors.addAll(associationSummary.getAssociationValidationErrors());
        }
        return associationValidationErrors;
    }
}