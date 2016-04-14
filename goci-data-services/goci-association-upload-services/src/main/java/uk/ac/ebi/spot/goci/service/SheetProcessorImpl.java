package uk.ac.ebi.spot.goci.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import utils.SheetCellProcessingService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author emma
 *         <p>
 *         This class takes an Excel spreadsheet sheet and extracts all records. For each record, a row object is
 *         created containing association information.
 */
@Service
public class SheetProcessorImpl implements UploadSheetProcessor {

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    // Read and parse uploaded spreadsheet
    @Override public Collection<AssociationUploadRow> readSheetRows(XSSFSheet sheet) {

        XSSFRow headerRow = sheet.getRow(0);
        Map<Integer, String> headerRowMap = createHeaderMap(headerRow);

        // Create collection to store all newly created rows
        Collection<AssociationUploadRow> associationUploadRows = new ArrayList<>();
        Integer lastRow = sheet.getLastRowNum();
        Integer rowNum = 1;

        while (rowNum <= lastRow) {

            AssociationUploadRow associationUploadRow = new AssociationUploadRow();
            associationUploadRow.setRowNumber(rowNum);

            for (Map.Entry<Integer, String> heading : headerRowMap.entrySet()) {
                Integer colNum = heading.getKey();
                String headerName = heading.getValue();
                XSSFRow row = sheet.getRow(rowNum);

                switch (headerName) {
                    case "SNP ID":

                        if (row.getCell(colNum, Row.RETURN_BLANK_AS_NULL) != null) {
                            associationUploadRow.setSnp(row.getCell(colNum)
                                                                .getRichStringCellValue()
                                                                .getString()
                                                                .trim());
                        }

                        break;
                    case "effect allele":
                        if (row.getCell(colNum, Row.RETURN_BLANK_AS_NULL) != null) {
                            associationUploadRow.setStrongestAllele(row.getCell(colNum)
                                                                            .getRichStringCellValue()
                                                                            .getString()
                                                                            .trim());
                        }
                        break;
                    case "other allele":
                        // TODO ADD
                        break;
                    case "effect allele frequency in controls":
                        if (row.getCell(colNum, Row.RETURN_BLANK_AS_NULL) != null) {
                            associationUploadRow.setAssociationRiskFrequency(row.getCell(colNum)
                                                                                     .getRichStringCellValue()
                                                                                     .getString()
                                                                                     .trim());
                        }
                        break;
                    case "gene":
                        if (row.getCell(colNum, Row.RETURN_BLANK_AS_NULL) != null) {
                            associationUploadRow.setAuthorReportedGene(row.getCell(colNum)
                                                                               .getRichStringCellValue()
                                                                               .getString()
                                                                               .trim());
                        }
                        break;
                    case "p-value mantissa":
                        associationUploadRow.setPvalueMantissa(SheetCellProcessingService.processIntValues(row.getCell(
                                colNum,
                                Row.RETURN_BLANK_AS_NULL)));
                        break;
                    case "p-value exponent":
                        associationUploadRow.setPvalueExponent(SheetCellProcessingService.processIntValues(row.getCell(
                                colNum,
                                Row.RETURN_BLANK_AS_NULL)));
                        break;
                    case "OR":
                        associationUploadRow.setOrPerCopyNum(SheetCellProcessingService.processFloatValues(row.getCell(
                                colNum,
                                Row.RETURN_BLANK_AS_NULL)));
                        break;
                    case "beta":
                        associationUploadRow.setBetaNum(SheetCellProcessingService.processFloatValues(row.getCell(
                                colNum,
                                Row.RETURN_BLANK_AS_NULL)));
                        break;
                    case "beta unit":
                        if (row.getCell(colNum, Row.RETURN_BLANK_AS_NULL) != null) {
                            associationUploadRow.setBetaUnit(row.getCell(colNum)
                                                                     .getRichStringCellValue()
                                                                     .getString()
                                                                     .trim());
                        }
                        break;
                    case "beta direction":
                        if (row.getCell(colNum, Row.RETURN_BLANK_AS_NULL) != null) {
                            associationUploadRow.setBetaDirection(row.getCell(colNum)
                                                                          .getRichStringCellValue()
                                                                          .getString()
                                                                          .trim());
                        }
                        break;
                    case "OR/beta SE":
                        associationUploadRow.setStandardError(SheetCellProcessingService.processFloatValues(row.getCell(
                                colNum,
                                Row.RETURN_BLANK_AS_NULL)));
                        break;
                    case "OR/beta range":
                        if (row.getCell(colNum, Row.RETURN_BLANK_AS_NULL) != null) {
                            associationUploadRow.setRange(row.getCell(colNum)
                                                                  .getRichStringCellValue()
                                                                  .getString()
                                                                  .trim());
                        }
                    default:
                        getLog().warn("Column with heading " + headerName + " found in file.");
                        break;
                }
            }
            associationUploadRows.add(associationUploadRow);
            rowNum++;
        }
        return associationUploadRows;
    }

    @Override public Map<Integer, String> createHeaderMap(XSSFRow row) {
        Map<Integer, String> headerMap = new HashMap<>();

        if (row.getPhysicalNumberOfCells() != 0) {

            short minColIx = row.getFirstCellNum();
            short maxColIx = row.getLastCellNum();
            for (short colIx = minColIx; colIx < maxColIx; colIx++) {
                XSSFCell cell = row.getCell(colIx);
                headerMap.put((int) colIx, cell.getStringCellValue());
            }
        }
        else {
            getLog().error("Header column contains no cells");
        }
        return headerMap;
    }
}