package uk.ac.ebi.spot.goci.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.utils.SheetCellProcessingService;

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
                XSSFCell cell = row.getCell(colNum, Row.RETURN_BLANK_AS_NULL);

                switch (headerName) {
                    case "SNP ID":
                        if (cell != null) {
                            associationUploadRow.setSnp(cell.getRichStringCellValue()
                                                                .getString()
                                                                .trim());
                        }
                        break;
                    case "effect allele":
                        if (cell != null) {
                            associationUploadRow.setStrongestAllele(cell.getRichStringCellValue()
                                                                            .getString()
                                                                            .trim());
                        }
                        break;
                    case "other allele":
                        if (cell != null) {
                            associationUploadRow.setOtherAllele(cell.getRichStringCellValue()
                                                                        .getString()
                                                                        .trim());
                        }
                        break;
                    case "effect allele frequency in controls":
                        if (cell != null) {
                            associationUploadRow.setAssociationRiskFrequency(SheetCellProcessingService.processStringValue(
                                    cell));
                        }
                        break;
                    case "gene":
                        if (cell != null) {
                            associationUploadRow.setAuthorReportedGene(cell.getRichStringCellValue()
                                                                               .getString()
                                                                               .trim());
                        }
                        break;
                    case "p-value mantissa":
                        associationUploadRow.setPvalueMantissa(SheetCellProcessingService.processIntValues(cell));
                        break;
                    case "p-value exponent":
                        associationUploadRow.setPvalueExponent(SheetCellProcessingService.processIntValues(cell));
                        break;
                    case "OR":
                        associationUploadRow.setOrPerCopyNum(SheetCellProcessingService.processFloatValues(cell));
                        break;
                    case "beta":
                        associationUploadRow.setBetaNum(SheetCellProcessingService.processFloatValues(cell));
                        break;
                    case "beta unit":
                        if (cell != null) {
                            associationUploadRow.setBetaUnit(cell.getRichStringCellValue()
                                                                     .getString()
                                                                     .trim());
                        }
                        break;
                    case "beta direction":
                        if (cell != null) {
                            associationUploadRow.setBetaDirection(cell.getRichStringCellValue()
                                                                          .getString()
                                                                          .trim());
                        }
                        break;
                    case "OR/beta SE":
                        associationUploadRow.setStandardError(SheetCellProcessingService.processFloatValues(cell));
                        break;
                    case "OR/beta range":
                        if (cell != null) {
                            associationUploadRow.setRange(cell.getRichStringCellValue()
                                                                  .getString()
                                                                  .trim());
                        }
                        break;
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