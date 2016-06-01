package uk.ac.ebi.spot.goci.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.utils.SheetCellProcessingService;
import uk.ac.ebi.spot.goci.utils.TranslateUploadHeaders;
import uk.ac.ebi.spot.goci.utils.UploadFileHeader;

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
@Lazy
public class SheetProcessorImpl implements UploadSheetProcessor {

    private TranslateUploadHeaders translateUploadHeaders;

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public SheetProcessorImpl(TranslateUploadHeaders translateUploadHeaders) {
        this.translateUploadHeaders = translateUploadHeaders;
    }

    // Read and parse uploaded spreadsheet
    @Override public Collection<AssociationUploadRow> readSheetRows(XSSFSheet sheet) {

        XSSFRow headerRow = sheet.getRow(0);
        Map<Integer, UploadFileHeader> headerRowMap = createHeaderMap(headerRow);

        // Create collection to store all newly created rows
        Collection<AssociationUploadRow> associationUploadRows = new ArrayList<>();
        Integer lastRow = sheet.getLastRowNum();
        Integer rowNum = 1;

        while (rowNum <= lastRow) {
            AssociationUploadRow associationUploadRow = new AssociationUploadRow();
            associationUploadRow.setRowNumber(rowNum);
            int blankCellCount = 0;
            XSSFRow row = sheet.getRow(rowNum);

            for (Map.Entry<Integer, UploadFileHeader> heading : headerRowMap.entrySet()) {
                Integer colNum = heading.getKey();
                UploadFileHeader headerName = heading.getValue();
                XSSFCell cell = row.getCell(colNum, Row.RETURN_BLANK_AS_NULL);

                if (cell == null) {
                    blankCellCount++;
                }
                else {
                    switch (headerName) {
                        case SNP_ID:
                            associationUploadRow.setSnp(cell.getRichStringCellValue()
                                                                .getString()
                                                                .trim());
                            break;
                        case EFFECT_ALLELE:
                            associationUploadRow.setStrongestAllele(cell.getRichStringCellValue()
                                                                            .getString()
                                                                            .trim());
                            break;
                        case OTHER_ALLELES:
                            associationUploadRow.setOtherAllele(cell.getRichStringCellValue()
                                                                        .getString()
                                                                        .trim());
                            break;
                        case EFFECT_ALLELE_FREQUENCY_IN_CONTROLS:
                            associationUploadRow.setAssociationRiskFrequency(SheetCellProcessingService.processStringValue(
                                    cell));
                            break;
                        case PVALUE_MANTISSA:
                            associationUploadRow.setPvalueMantissa(SheetCellProcessingService.processIntValues(cell));
                            break;
                        case PVALUE_EXPONENT:
                            associationUploadRow.setPvalueExponent(SheetCellProcessingService.processIntValues(cell));
                            break;
                        case OR:
                            associationUploadRow.setOrPerCopyNum(SheetCellProcessingService.processFloatValues(cell));
                            break;
                        case BETA:
                            associationUploadRow.setBetaNum(SheetCellProcessingService.processFloatValues(cell));
                            break;
                        case BETA_UNIT:
                            associationUploadRow.setBetaUnit(cell.getRichStringCellValue()
                                                                     .getString()
                                                                     .trim());
                            break;
                        case BETA_DIRECTION:
                            associationUploadRow.setBetaDirection(cell.getRichStringCellValue()
                                                                          .getString()
                                                                          .trim());
                            break;
                        case STANDARD_ERROR:
                            associationUploadRow.setStandardError(SheetCellProcessingService.processFloatValues(cell));
                            break;
                        case RANGE:
                            associationUploadRow.setRange(cell.getRichStringCellValue()
                                                                  .getString()
                                                                  .trim());
                            break;
                        case PVALUE_DESCRIPTION:
                            associationUploadRow.setPvalueDescription(cell.getRichStringCellValue()
                                                                  .getString()
                                                                  .trim());
                            break;
                        default:
                            getLog().warn("Column with unknown heading found in file.");
                            break;
                    }
                }
            }

            // If all cells in line aren't blank
            if (blankCellCount != row.getPhysicalNumberOfCells()) {
                associationUploadRows.add(associationUploadRow);
            }
            rowNum++;
        }
        return associationUploadRows;
    }

    @Override public Map<Integer, UploadFileHeader> createHeaderMap(XSSFRow row) {
        Map<Integer, UploadFileHeader> headerMap = new HashMap<>();

        if (row.getPhysicalNumberOfCells() != 0) {

            short minColIx = row.getFirstCellNum();
            short maxColIx = row.getLastCellNum();
            for (short colIx = minColIx; colIx < maxColIx; colIx++) {
                XSSFCell cell = row.getCell(colIx);
                UploadFileHeader headerType =
                        translateUploadHeaders.translateToEnumValue(cell.getStringCellValue().trim());
                headerMap.put((int) colIx, headerType);
            }
        }
        else {
            getLog().error("Header column contains no cells");
        }
        return headerMap;
    }
}