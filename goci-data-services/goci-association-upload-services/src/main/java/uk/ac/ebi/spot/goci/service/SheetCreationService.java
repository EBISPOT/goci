package uk.ac.ebi.spot.goci.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by emma on 13/04/2016.
 * @author emma
 */
@Service
public class SheetCreationService {

    /**
     * Create sheet from file, this is then used to read through each row
     *
     * @param fileName Name of XLSX file supplied by user
     */
    public XSSFSheet createSheet(String fileName) throws InvalidFormatException, IOException {
        // Open file
        OPCPackage pkg = OPCPackage.open(fileName);
        XSSFWorkbook current = new XSSFWorkbook(pkg);
        pkg.close(); // TODO WILL THIS CAUSE ISSUES?
        return current.getSheetAt(0);
    }
}