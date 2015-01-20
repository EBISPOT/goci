package uk.ac.ebi.spot.goci.service;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;

import java.util.ArrayList;


/**
 * @author emma
 *         <p>
 *         This is a small program to upload a batch of SNPs from a .xlsx spreadsheet.
 *         Note that the spreadsheet must be of .xlsx format!
 *         <p>
 *         Created from code originally written by Dani. Adapted to fit with new curation system.
 *         <p>
 */
@Service
public class SnpBatchLoaderService {


    public SnpBatchLoaderService() {

    }

    public ArrayList<Association> processData(String name) throws Exception {
        String filename = name;

        XSSFSheet sheet = null;
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFWorkbook current = new XSSFWorkbook(pkg);
        sheet = current.getSheetAt(0);
        SNPSheetProcessor processor = null;

        try {
            processor = new SNPSheetProcessor(sheet);
            ArrayList<Association> associations = processor.getSNPlist();
            pkg.close();
            return associations;
        } catch (Exception e) {

            // TODO CREATE EXCEPTION IF THIS GOES WRONG
            e.printStackTrace();
            throw e;
        }
    }
}