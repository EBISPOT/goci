package uk.ac.ebi.spot.goci.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author emma
 *         <p>
 *         This class takes an Excel spreadsheet sheet and extracts all the association records
 *         For each SNP, an Association object is created
 *         <p>
 *         Created from code originally written by Dani. Adapted to fit with new curation system.
 */

public class AssociationSheetProcessor {

    private XSSFSheet sheet;
    private ArrayList<Association> allSnpAssociations = new ArrayList<Association>();
    private AssociationCalculationService associationCalculationService = new AssociationCalculationService();
    private Logger log = LoggerFactory.getLogger(getClass());
    private String logMessage;

    protected Logger getLog() {
        return log;
    }

    public AssociationSheetProcessor(XSSFSheet sheet) {
        this.sheet = sheet;
        logMessage = "";

        // Read through sheet and extract values
        readSnpAssociations();
    }

    public void readSnpAssociations() {
        boolean done = false;
        int rowNum = 1;


        while (!done) {
            XSSFRow row = sheet.getRow(rowNum);

            if (row == null) {
                done = true;
                getLog().debug("Last row read");
                logMessage = "All spreadsheet data processed successfully";
            } else {

                String authorReportedGene;
                String strongestAllele;
                String snp;

                // Create a collection to hold newly identified SNPs
                Collection<SingleNucleotidePolymorphism> snps = new ArrayList<>();
                Collection<Gene> reportedGenes = new ArrayList<>();


                if (row.getCell(0, row.RETURN_BLANK_AS_NULL) != null) {
                    authorReportedGene = row.getCell(0).getRichStringCellValue().getString();

                    Gene reportedGene = new Gene();
                    reportedGenes.add(reportedGene);
                    logMessage = "Error in field 'Gene' in row " + rowNum + 1 + "\n";

                } else {
                    authorReportedGene = null;
                    getLog().debug("Gene is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Gene' in row " + rowNum + 1 + "\n";
                }

                if (row.getCell(1, row.RETURN_BLANK_AS_NULL) != null) {
                    strongestAllele = row.getCell(1).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Risk allele' in row " + rowNum + 1 + "\n";

                } else {
                    strongestAllele = null;
                    getLog().debug("Risk allele is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk allele' in row " + rowNum + 1 + "\n";
                }

                if (row.getCell(2, row.RETURN_BLANK_AS_NULL) != null) {
                    snp = row.getCell(2).getRichStringCellValue().getString();

                /* TODO COMMENTING OUT UNTIL WE KNOW MORE ABOUT HANDLING HOW TO HANDLE MULTIPLE SNPS

                // More than one SNP entered in row, user should separate these with comma
                    if (snp.contains(",")) {
                        String[] splitSnpStrings = snp.split(",");
                        for (String splitSnpString : splitSnpStrings) {
                            // Create a new SNP object
                            SingleNucleotidePolymorphism newSnp = new SingleNucleotidePolymorphism();
                            newSnp.setRsId(splitSnpString);

                            // Add to collection
                            snps.add(newSnp);
                        }
                    }*/

                    // Single snp entered, assuming string with no comma is a single entry

                    SingleNucleotidePolymorphism newSnp = new SingleNucleotidePolymorphism();
                    newSnp.setRsId(snp);

                    // Add to collection
                    snps.add(newSnp);

                    logMessage = "Error in field 'SNP' in row " + rowNum + 1 + "\n";

                } else {
                    snp = null;
                    getLog().debug("SNP is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP' in row " + rowNum + 1 + "\n";

                }

                String riskFrequency = null;

                if (row.getCell(3, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell risk = row.getCell(3);
                    switch (risk.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            riskFrequency = risk.getRichStringCellValue().getString();
                            logMessage = "Error in field 'Risk Frequency' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            riskFrequency = Double.toString(risk.getNumericCellValue());
                            logMessage = "Error in field 'Risk Frequency' in row " + rowNum + 1 + "\n";

                            break;
                    }
                } else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk Frequency' in row " + rowNum + 1 + "\n";
                }

                Integer pvalueMantissa = null;
                Integer pvalueExponent = null;

                if (row.getCell(4, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(4);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueMantissa = null;
                            logMessage = "Error in field 'pvalue mantissa' in row " + rowNum + 1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueMantissa = (int) mant.getNumericCellValue();
                            logMessage = "Error in field 'pvalue mantissa' in row " + rowNum + 1 + "\n";

                            break;
                    }
                } else {
                    pvalueMantissa = null;
                    getLog().debug("pvalue mantissa is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue mantissa' in row " + rowNum + 1 + "\n";

                }

                if (row.getCell(5, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell expo = row.getCell(5);
                    switch (expo.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueExponent = null;
                            logMessage = "Error in field 'pvalue exponent' in row " + rowNum + 1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueExponent = (int) expo.getNumericCellValue();
                            logMessage = "Error in field 'pvalue exponent' in row " + rowNum + 1 + "\n";

                            break;
                    }
                } else {
                    pvalueExponent = null;
                    getLog().debug("pvalue exponent is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue exponent' in row " + rowNum + 1 + "\n";
                }

                // This is calculated from mantissa and exponent
                Float pvalueFloat;

                if (pvalueExponent != null && pvalueMantissa != null) {
                    pvalueFloat = associationCalculationService.calculatePvalueFloat(pvalueMantissa, pvalueExponent);
                } else {
                    pvalueFloat = Float.valueOf(0);
                }


                String pvalueText;
                if (row.getCell(6, row.RETURN_BLANK_AS_NULL) != null) {
                    pvalueText = row.getCell(6).getRichStringCellValue().getString();
                    logMessage = "Error in field 'pvaluetxt' in row " + rowNum + 1 + "\n";

                } else {
                    pvalueText = null;
                    getLog().debug("pvalue text is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvaluetxt' in row " + rowNum + 1 + "\n";
                }

                Float orPerCopyNum = null;
                if (row.getCell(7, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell or = row.getCell(7);
                    switch (or.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyNum = null;
                            logMessage = "Error in field 'OR' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyNum = Float.valueOf((float) or.getNumericCellValue());
                            logMessage = "Error in field 'OR' in row " + rowNum + 1 + "\n";
                            break;
                    }
                } else {
                    orPerCopyNum = null;
                    getLog().debug("OR is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR' in row " + rowNum + 1 + "\n";
                }

                Float orPerCopyRecip = null;
                if (row.getCell(8, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell recip = row.getCell(8);
                    switch (recip.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyRecip = null;
                            logMessage = "Error in field 'OR recip' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyRecip = Float.valueOf((float) recip.getNumericCellValue());
                            logMessage = "Error in field 'OR recip' in row " + rowNum + 1 + "\n";
                            break;
                    }
                } else {
                    orPerCopyRecip = null;
                    getLog().debug("OR recip is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR recip' in row " + rowNum + 1 + "\n";

                }

                // If reciprocal is given, program will calculate OR from that
                // This logic is retained from Dani's original code
                boolean recipReverse = false;
                if ((orPerCopyRecip != null) && (orPerCopyNum == null)) {
                    orPerCopyNum = ((100 / orPerCopyRecip) / 100);
                    recipReverse = true;
                }

                String orType;
                if (row.getCell(9, row.RETURN_BLANK_AS_NULL) != null) {
                    orType = row.getCell(9).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR type' in row " + rowNum + 1 + "\n";
                } else {
                    orType = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR type' in row " + rowNum + 1 + "\n";
                }

                String orPerCopyRange;
                if (row.getCell(10, row.RETURN_BLANK_AS_NULL) != null) {
                    orPerCopyRange = row.getCell(10).getRichStringCellValue().getString();
                    logMessage = "Error in field 'CI' in row " + rowNum + 1 + "\n";
                } else {
                    orPerCopyRange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                    logMessage = "Error in field 'CI' in row " + rowNum + 1 + "\n";
                }

                String orPerCopyUnitDescr;
                if (row.getCell(11) != null) {
                    orPerCopyUnitDescr = row.getCell(11).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR direction' in row " + rowNum + 1 + "\n";
                } else {
                    orPerCopyUnitDescr = null;
                    getLog().debug("OR direction is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR direction' in row " + rowNum + 1 + "\n";
                }

                Float orPerCopyStdError = null;

                if (row.getCell(12, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell std = row.getCell(12);
                    switch (std.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyStdError = null;
                            logMessage = "Error in field 'Standard Error' in row " + rowNum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyStdError = Float.valueOf((float) std.getNumericCellValue());
                            logMessage = "Error in field 'Standard Error' in row " + rowNum + 1 + "\n";
                            break;
                    }
                } else {
                    orPerCopyStdError = null;
                    getLog().debug("SE is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Standard Error' in row " + rowNum + 1 + "\n";
                }


                // This logic is retained from Dani's original code
                if ((orPerCopyRecip != null) && (orPerCopyRange != null) && recipReverse) {
                    orPerCopyRange = associationCalculationService.reverseCI(orPerCopyRange);
                }


                if ((orPerCopyRange == null) && (orPerCopyStdError != null)) {
                    orPerCopyRange = associationCalculationService.setRange(orPerCopyStdError, orPerCopyNum);
                }


                String snpType;
                if (row.getCell(13, row.RETURN_BLANK_AS_NULL) != null) {
                    snpType = row.getCell(13).getRichStringCellValue().getString();
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                } else {
                    snpType = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                }

                String multiSnpHaplotype;
                if (row.getCell(13, row.RETURN_BLANK_AS_NULL) != null) {
                    multiSnpHaplotype = row.getCell(13).getRichStringCellValue().getString();
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                } else {
                    multiSnpHaplotype = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                }

                String snpInteraction;
                if (row.getCell(13, row.RETURN_BLANK_AS_NULL) != null) {
                    snpInteraction = row.getCell(13).getRichStringCellValue().getString();
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                } else {
                    snpInteraction = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                }


                if (authorReportedGene == null && strongestAllele == null && snp == null && riskFrequency == null) {
                    done = true;
                    getLog().debug("Empty row that wasn't caught via 'row = null'");
                } else {
                    // Create a new association
                    Association thisAssociation = new Association();
                 //   thisAssociation.setAuthorReportedGene(authorReportedGene);
                    thisAssociation.setRiskFrequency(riskFrequency);
                    thisAssociation.setPvalueFloat(pvalueFloat);
                    thisAssociation.setPvalueText(pvalueText);
                    thisAssociation.setOrPerCopyNum(orPerCopyNum);
                    thisAssociation.setOrType(orType);
                    thisAssociation.setSnpType(snpType);
                    thisAssociation.setMultiSnpHaplotype(multiSnpHaplotype);
                    thisAssociation.setSnpInteraction(snpInteraction);
                    thisAssociation.setPvalueMantissa(pvalueMantissa);
                    thisAssociation.setPvalueExponent(pvalueExponent);
                    thisAssociation.setOrPerCopyRecip(orPerCopyRecip);
                    thisAssociation.setOrPerCopyStdError(orPerCopyStdError);
                    thisAssociation.setOrPerCopyRange(orPerCopyRange);
                    thisAssociation.setOrPerCopyUnitDescr(orPerCopyUnitDescr);


                    // Add all newly created associations to collection
                    allSnpAssociations.add(thisAssociation);
                }

            }
            rowNum++;
        }
    }

    public ArrayList<Association> getAllSnpAssociations() {
        return allSnpAssociations;
    }

    public String getLogMessage() {
        return logMessage;
    }


}
