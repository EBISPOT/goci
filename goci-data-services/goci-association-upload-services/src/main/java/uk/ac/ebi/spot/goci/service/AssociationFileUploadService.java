package uk.ac.ebi.spot.goci.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

import java.io.File;
import java.io.IOException;
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

    private SheetProcessor sheetProcessor;

    private AssociationRowProcessor associationRowProcessor;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public AssociationFileUploadService(SheetProcessor sheetProcessor,
                                        AssociationRowProcessor associationRowProcessor) {
        this.sheetProcessor = sheetProcessor;
        this.associationRowProcessor = associationRowProcessor;
    }

    /**
     * Process uploaded file
     *
     * @param file XLSX file supplied by user
     */
    public void processAssociationFile(File file) {

        if (!file.exists()) {
            // Create sheet
            XSSFSheet sheet = null;
            try {
                sheet = createSheet(file.getAbsolutePath());

                // Process file
                Collection<AssociationUploadRow> fileRows = sheetProcessor.readSheetRows(sheet);

                // Error check each row
                // Create a map of row and resulting association
                for (AssociationUploadRow row: fileRows){

                    Integer rowNumber = row.getRowNumber();
                    String effectType = row.getEffectType();
                    Association association = associationRowProcessor.createAssociationFromUploadRow(row);



                }
            }
            catch (InvalidFormatException | IOException e) {
                getLog().error("File: " + file.getName() + " cannot be processed", e);
            }
        }
        else {
            getLog().error("File: " + file.getName() + " cannot be found");
        }
    }

    /**
     * Create sheet from file, this is then used to read through each row
     *
     * @param fileName Name of XLSX file supplied by user
     */
    private XSSFSheet createSheet(String fileName) throws InvalidFormatException, IOException {

        // Open file
        OPCPackage pkg = OPCPackage.open(fileName);
        XSSFWorkbook current = new XSSFWorkbook(pkg);
        pkg.close(); // TODO WILL THIS CAUSE ISSUES?
        return current.getSheetAt(0);
    }
}