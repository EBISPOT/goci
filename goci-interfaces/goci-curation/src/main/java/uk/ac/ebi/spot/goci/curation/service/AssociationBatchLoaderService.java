package uk.ac.ebi.spot.goci.curation.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


/**
 * @author emma
 *         <p>
 *         This is a small program to upload a batch of SNPs from a .xlsx spreadsheet. Note that the spreadsheet must be
 *         of .xlsx format!
 *         <p>
 *         Created from code originally written by Dani. Adapted to fit with new curation system.
 *         <p>
 */

@Service
public class AssociationBatchLoaderService {

    private AssociationSheetProcessor associationSheetProcessor;

    @Autowired
    public AssociationBatchLoaderService(AssociationSheetProcessor associationSheetProcessor) {
        this.associationSheetProcessor = associationSheetProcessor;
    }

    // Returns an array list of new association forms, the controller will turn
    // these into associations and save
    public Collection<Association> processData(String fileName)
            throws InvalidFormatException, IOException, InvalidOperationException, RuntimeException {

        // Open and parse our spreadsheet file
        XSSFSheet sheet = null;
        OPCPackage pkg = OPCPackage.open(fileName);
        XSSFWorkbook current = new XSSFWorkbook(pkg);
        sheet = current.getSheetAt(0);
        AssociationSheetProcessor processor = null;
        try {
            associationSheetProcessor.readSnpAssociations(sheet);
            Collection<Association> associations = associationSheetProcessor.getAllAssociations();
            pkg.close();
            return associations;
        }
        finally {
            // Delete our file
            File fileToDelete = new File(fileName);
            fileToDelete.deleteOnExit();
        }
    }

}