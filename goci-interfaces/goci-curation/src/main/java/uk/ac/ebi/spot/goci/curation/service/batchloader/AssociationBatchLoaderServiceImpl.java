package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.service.AssociationSheetProcessor;
import uk.ac.ebi.spot.goci.model.Association;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

@Service
public class AssociationBatchLoaderServiceImpl implements AssociationBatchLoaderService {

    private AssociationSheetProcessor associationSheetProcessor;

    @Autowired
    public AssociationBatchLoaderServiceImpl(AssociationSheetProcessor associationSheetProcessor) {
        this.associationSheetProcessor = associationSheetProcessor;
    }

    // Returns an array list of new association forms, the controller will turn
    // these into associations and save
    @Override public Collection<Association> processData(String fileName)
            throws InvalidFormatException, IOException, RuntimeException {

        // Open and parse our spreadsheet file
        XSSFSheet sheet;
        OPCPackage pkg = OPCPackage.open(fileName);
        XSSFWorkbook current = new XSSFWorkbook(pkg);
        sheet = current.getSheetAt(0);
        try {
            Collection<Association> associations = associationSheetProcessor.readSnpAssociations(sheet);
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