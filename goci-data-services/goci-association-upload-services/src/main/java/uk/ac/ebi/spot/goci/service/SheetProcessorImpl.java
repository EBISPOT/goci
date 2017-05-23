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
import uk.ac.ebi.spot.goci.exception.CellProcessingException;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.ValidationError;
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

            // Set row number so its consistent with numbering curator will see via Excel
            associationUploadRow.setRowNumber(rowNum + 1);
            XSSFRow row = sheet.getRow(rowNum);

            // If the row contains defined cell values
            if (row.getPhysicalNumberOfCells() > 0) {
                for (Map.Entry<Integer, UploadFileHeader> heading : headerRowMap.entrySet()) {
                    Integer colNum = heading.getKey();
                    UploadFileHeader headerName = heading.getValue();
                    XSSFCell cell = row.getCell(colNum, Row.RETURN_BLANK_AS_NULL);

                    if (cell != null) {
                        try {
                            switch (headerName) {
                                case GENES:
                                    associationUploadRow.setAuthorReportedGene(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case SNP:
                                    associationUploadRow.setSnp(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case EFFECT_ALLELE:
                                    associationUploadRow.setStrongestAllele(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case OTHER_ALLELES:
                                    associationUploadRow.setOtherAllele(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case PROXY_SNP:
                                    associationUploadRow.setProxy(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case EFFECT_ELEMENT_FREQUENCY_IN_CONTROLS:
                                    associationUploadRow.setAssociationRiskFrequency(SheetCellProcessingService.processStringValue(
                                            cell));
                                    break;
                                case INDEPENDENT_SNP_EFFECT_ALLELE_FREQUENCY_IN_CONTROLS:
                                    associationUploadRow.setRiskFrequency(SheetCellProcessingService.processStringValue(
                                            cell));
                                    break;
                                case PVALUE_MANTISSA:
                                    associationUploadRow.setPvalueMantissa(SheetCellProcessingService.processIntValues(cell));
                                    break;
                                case PVALUE_EXPONENT:
                                    associationUploadRow.setPvalueExponent(SheetCellProcessingService.processIntValues(cell));
                                    break;
                                case PVALUE_DESCRIPTION:
                                    associationUploadRow.setPvalueDescription(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case OR:
                                    associationUploadRow.setOrPerCopyNum(SheetCellProcessingService.processFloatValues(cell));
                                    break;
                                case OR_RECIPROCAL:
                                    associationUploadRow.setOrPerCopyRecip(SheetCellProcessingService.processFloatValues(cell));
                                    break;
                                case BETA:
                                    associationUploadRow.setBetaNum(SheetCellProcessingService.processFloatValues(cell));
                                    break;
                                case BETA_UNIT:
                                    associationUploadRow.setBetaUnit(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case BETA_DIRECTION:
                                    associationUploadRow.setBetaDirection(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case RANGE:
                                    associationUploadRow.setRange(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case OR_RECIPROCAL_RANGE:
                                    associationUploadRow.setOrPerCopyRecipRange(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case STANDARD_ERROR:
                                    associationUploadRow.setStandardError(SheetCellProcessingService.processFloatValues(cell));
                                    break;

                                case DESCRIPTION:
                                    associationUploadRow.setDescription(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case MULTI_SNP_HAPLOTYPE:
                                    associationUploadRow.setMultiSnpHaplotype(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case SNP_INTERACTION:
                                    associationUploadRow.setSnpInteraction(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case SNP_STATUS:
                                    associationUploadRow.setSnpStatus(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case SNP_TYPE:
                                    associationUploadRow.setSnpType(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                case EFO_TRAITS:
                                    associationUploadRow.setEfoTrait(SheetCellProcessingService.processMandatoryStringValue(cell));
                                    break;
                                default:
                                    getLog().warn("Column with unknown heading found in file.");
                                    break;
                            }
                        } catch (CellProcessingException cpe) {
                            // Add an excel error to the list of the errors.
                            ValidationError cpeValidationError = new ValidationError(headerName.toString(),cpe.getMessage(),false,"excel");
                            associationUploadRow.addCellErrorType(cpeValidationError);

                        }
                    }
                }
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