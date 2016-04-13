package uk.ac.ebi.spot.goci.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author emma
 *         <p>
 *         This class takes an Excel spreadsheet sheet and extracts all the association records. For each association, a
 *         row object is created.
 *         <p>
 *         Created from code originally written by Dani/Tony. Adapted to fit with new curation system.
 */
@Service
public class SheetProcessorImpl implements UploadSheetProcessor {

    private AssociationCalculationService associationCalculationService;

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public SheetProcessorImpl(AssociationCalculationService associationCalculationService) {
        this.associationCalculationService = associationCalculationService;
    }

    // Read and parse uploaded spreadsheet
    @Override public Collection<AssociationUploadRow> readSheetRows(XSSFSheet sheet) {

        XSSFRow headerRow = sheet.getRow(0);
        Map<Integer, String> headerRowMap = createHeaderMap(headerRow);

        // Create collection to store all newly created associations
        Collection<AssociationUploadRow> associationUploadRows = new ArrayList<>();

        boolean done = false;
        int rowNum = 1;


        while (!done) {
            XSSFRow row = sheet.getRow(rowNum);

            if (row == null) {
                done = true;
                getLog().debug("Last row read");
            }
            else {
                // Get gene values
                String authorReportedGene = null;
                if (row.getCell(0, row.RETURN_BLANK_AS_NULL) != null) {
                    authorReportedGene = row.getCell(0).getRichStringCellValue().getString().trim();
                }
                else {
                    getLog().debug("Gene is null in row " + row.getRowNum());
                }

                // Get Strongest SNP-Risk Allele
                String strongestAllele = null;
                if (row.getCell(1, row.RETURN_BLANK_AS_NULL) != null) {
                    strongestAllele = row.getCell(1).getRichStringCellValue().getString().trim();
                }
                else {
                    getLog().debug("Risk allele is null in row " + row.getRowNum());
                }

                // Get SNP
                String snp = null;
                if (row.getCell(2, row.RETURN_BLANK_AS_NULL) != null) {
                    snp = row.getCell(2).getRichStringCellValue().getString().trim();
                }
                else {
                    getLog().debug("SNP is null in row " + row.getRowNum());
                }

                // Get Proxy SNP
                String proxy = null;
                if (row.getCell(3, row.RETURN_BLANK_AS_NULL) != null) {
                    proxy = row.getCell(3).getRichStringCellValue().getString().trim();
                }
                else {
                    getLog().debug("Proxy SNP is null in row " + row.getRowNum());
                }

                // Get Risk Allele Frequency, will contain multiple values for SNP interaction studies
                String riskFrequency = null;
                if (row.getCell(4, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell risk = row.getCell(4);
                    switch (risk.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            riskFrequency = risk.getRichStringCellValue().getString().trim();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            riskFrequency = Double.toString(risk.getNumericCellValue());
                            break;
                    }
                }
                else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                }

                // Will be a single value that applies to association
                String associationRiskFrequency = null;
                if (row.getCell(5, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell risk = row.getCell(5);
                    switch (risk.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            associationRiskFrequency = risk.getRichStringCellValue().getString().trim();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            associationRiskFrequency = Double.toString(risk.getNumericCellValue());
                            break;
                    }
                }
                else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                }

                // Get P-value mantissa	and P-value exponent
                Integer pvalueMantissa = null;
                Integer pvalueExponent = null;

                if (row.getCell(6, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(6);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueMantissa = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueMantissa = (int) mant.getNumericCellValue();
                            break;
                    }
                }
                else {
                    pvalueMantissa = null;
                    getLog().debug("pvalue mantissa is null in row " + row.getRowNum());
                }

                if (row.getCell(7, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell expo = row.getCell(7);
                    switch (expo.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueExponent = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueExponent = (int) expo.getNumericCellValue();
                            break;
                    }
                }
                else {
                    pvalueExponent = null;
                    getLog().debug("pvalue exponent is null in row " + row.getRowNum());
                }

                // Get P-value description
                String pvalueDescription;
                if (row.getCell(8, row.RETURN_BLANK_AS_NULL) != null) {
                    pvalueDescription = row.getCell(8).getRichStringCellValue().getString().trim();
                }
                else {
                    pvalueDescription = null;
                    getLog().debug("pvalue text is null in row " + row.getRowNum());
                }

                // Get Effect type, this can be "OR", "Beta" or "NR"
                String effectType;
                if (row.getCell(9, row.RETURN_BLANK_AS_NULL) != null) {
                    effectType = row.getCell(9).getRichStringCellValue().getString().trim();
                }
                else {
                    // Set default value
                    effectType = "NR";
                    getLog().debug("Effect type is null in row " + row.getRowNum());
                }

                // Get OR num
                Float orPerCopyNum = null;
                if (row.getCell(10, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell or = row.getCell(10);
                    switch (or.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyNum = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyNum = (float) or.getNumericCellValue();
                            break;
                    }
                }
                else {
                    orPerCopyNum = null;
                    getLog().debug("OR is null in row " + row.getRowNum());
                }

                // Get reciprocal OR
                Float orPerCopyRecip = null;
                if (row.getCell(11, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell recip = row.getCell(11);
                    switch (recip.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyRecip = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyRecip = (float) recip.getNumericCellValue();
                            break;
                    }
                }
                else {
                    orPerCopyRecip = null;
                    getLog().debug("OR recip is null in row " + row.getRowNum());
                }

                // Get Beta
                Float betaNum = null;
                if (row.getCell(12, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell beta = row.getCell(12);
                    switch (beta.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            betaNum = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            betaNum = (float) beta.getNumericCellValue();
                            break;
                    }
                }
                else {
                    betaNum = null;
                    getLog().debug("Beta is null in row " + row.getRowNum());
                }

                // Get Beta unit
                String betaUnit;
                if (row.getCell(13, row.RETURN_BLANK_AS_NULL) != null) {
                    betaUnit = row.getCell(13).getRichStringCellValue().getString().trim();
                }
                else {
                    betaUnit = null;
                    getLog().debug("Beta unit is null in row " + row.getRowNum());
                }

                // Get Beta direction
                String betaDirection;
                if (row.getCell(14, row.RETURN_BLANK_AS_NULL) != null) {
                    betaDirection = row.getCell(14).getRichStringCellValue().getString().trim().toLowerCase();
                }
                else {
                    betaDirection = null;
                    getLog().debug("Beta direction is null in row " + row.getRowNum());
                }

                // Get confidence interval
                String range;
                if (row.getCell(15, row.RETURN_BLANK_AS_NULL) != null) {
                    range = row.getCell(15).getRichStringCellValue().getString().trim();
                }
                else {
                    range = null;
                    getLog().debug("Range is null in row " + row.getRowNum());
                }

                // Get OR recip range
                String orPerCopyRecipRange;
                if (row.getCell(16, row.RETURN_BLANK_AS_NULL) != null) {
                    orPerCopyRecipRange = row.getCell(16).getRichStringCellValue().getString().trim();
                }
                else {
                    orPerCopyRecipRange = null;
                    getLog().debug("OR recip range is null in row " + row.getRowNum());
                }

                // Get standard error
                Float standardError = null;
                if (row.getCell(17, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell std = row.getCell(17);
                    switch (std.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            standardError = null;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            standardError = (float) std.getNumericCellValue();
                            break;
                    }
                }
                else {
                    standardError = null;
                    getLog().debug("SE is null in row " + row.getRowNum());
                }

                // Get description
                String description;
                if (row.getCell(18, row.RETURN_BLANK_AS_NULL) != null) {
                    description = row.getCell(18).getRichStringCellValue().getString().trim();
                }
                else {
                    description = null;
                    getLog().debug("Description is null in row " + row.getRowNum());
                }

                // Get Multi-SNP Haplotype value
                String multiSnpHaplotype;
                if (row.getCell(19, row.RETURN_BLANK_AS_NULL) != null) {
                    multiSnpHaplotype = row.getCell(19).getRichStringCellValue().getString().trim();
                }
                else {
                    multiSnpHaplotype = null;
                    getLog().debug("Multi-SNP Haplotype is null in row " + row.getRowNum());
                }

                // Get SNP interaction value
                String snpInteraction;
                if (row.getCell(20, row.RETURN_BLANK_AS_NULL) != null) {
                    snpInteraction = row.getCell(20).getRichStringCellValue().getString().trim();
                }
                else {
                    snpInteraction = null;
                    getLog().debug("SNP interaction is null in row " + row.getRowNum());
                }

                // Get SNP Status
                String snpStatus;
                if (row.getCell(21, row.RETURN_BLANK_AS_NULL) != null) {
                    snpStatus = row.getCell(21).getRichStringCellValue().getString().toLowerCase().trim();
                }
                else {
                    snpStatus = null;
                    getLog().debug("SNP status is null in row " + row.getRowNum());
                }

                // Get SNP type (novel / known)
                String snpType;
                if (row.getCell(22, row.RETURN_BLANK_AS_NULL) != null) {
                    snpType = row.getCell(22).getRichStringCellValue().getString().toLowerCase().trim();
                }
                else {
                    snpType = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                }

                String efoTrait;
                if (row.getCell(23, row.RETURN_BLANK_AS_NULL) != null) {
                    efoTrait = row.getCell(23).getRichStringCellValue().getString().trim();
                }
                else {
                    efoTrait = null;
                    getLog().debug("EFO trait is null in row " + row.getRowNum());
                }

                // Once we have all the values entered in file process them
                if (authorReportedGene == null && strongestAllele == null && snp == null && proxy == null &&
                        riskFrequency == null) {
                    done = true;
                    getLog().debug("Empty row that wasn't caught via 'row = null'");
                }
                else {
                    // Create row
                    AssociationUploadRow associationUploadRow = new AssociationUploadRow();
                    associationUploadRow.setRowNumber(rowNum);
                    associationUploadRow.setAuthorReportedGene(authorReportedGene);
                    associationUploadRow.setStrongestAllele(strongestAllele);
                    associationUploadRow.setSnp(snp);
                    associationUploadRow.setProxy(proxy);
                    associationUploadRow.setRiskFrequency(riskFrequency);
                    associationUploadRow.setAssociationRiskFrequency(associationRiskFrequency);
                    associationUploadRow.setPvalueMantissa(pvalueMantissa);
                    associationUploadRow.setPvalueExponent(pvalueExponent);
                    associationUploadRow.setPvalueDescription(pvalueDescription);
                    associationUploadRow.setEffectType(effectType);
                    associationUploadRow.setDescription(description);
                    associationUploadRow.setMultiSnpHaplotype(multiSnpHaplotype);
                    associationUploadRow.setSnpInteraction(snpInteraction);
                    associationUploadRow.setSnpStatus(snpStatus);
                    associationUploadRow.setSnpType(snpType);
                    associationUploadRow.setEfoTrait(efoTrait);

                    // Set beta values
                    associationUploadRow.setBetaNum(betaNum);
                    associationUploadRow.setBetaUnit(betaUnit);
                    associationUploadRow.setBetaDirection(betaDirection);

                    // Set OR num values
                    associationUploadRow.setOrPerCopyRecip(orPerCopyRecip);

                    // Calculate OR num if OR recip is present , otherwise set to whatever is in upload
                    boolean recipReverse = false;
                    if ((orPerCopyRecip != null) && (orPerCopyNum == null)) {
                        associationUploadRow.setOrPerCopyNum(((100 / orPerCopyRecip) / 100));
                        recipReverse = true;
                    }
                    else {
                        associationUploadRow.setOrPerCopyNum(orPerCopyNum);
                    }

                    associationUploadRow.setOrPerCopyRecipRange(orPerCopyRecipRange);
                    associationUploadRow.setStandardError(standardError);

                    // Calculate range
                    // (This logic is retained from Dani's original code)
                    if ((orPerCopyRecipRange != null) && recipReverse) {
                        associationUploadRow.setRange(associationCalculationService.reverseCI(orPerCopyRecipRange));
                    }
                    else if ((range == null) && (standardError != null)) {
                        if (effectType.equals("OR")) {
                            if (orPerCopyNum != null) {
                                associationUploadRow.setRange(associationCalculationService.setRange(standardError,
                                                                                                     orPerCopyNum));
                            }
                        }

                        if (effectType.equals("Beta")) {
                            if (betaNum != null) {
                                associationUploadRow.setRange(associationCalculationService.setRange(standardError,
                                                                                                     betaNum));
                            }
                        }
                    }
                    else {
                        associationUploadRow.setRange(range);
                    }

                    // Add row to collection
                    associationUploadRows.add(associationUploadRow);
                }
                rowNum++;
            }
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