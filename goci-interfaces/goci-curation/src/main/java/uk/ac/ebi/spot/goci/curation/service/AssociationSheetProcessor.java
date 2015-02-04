package uk.ac.ebi.spot.goci.curation.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author emma
 *         <p/>
 *         This class takes an Excel spreadsheet sheet and extracts all the association records
 *         For each SNP, an SnpAssociationForm object is created and passed back to the controller for
 *         further processing
 *         <p/>
 *         Created from code originally written by Dani/Tony. Adapted to fit with new curation system.
 */

public class AssociationSheetProcessor {

    // Store forms to pass back to controller
    private Collection<SnpAssociationForm> snpAssociationForms = new ArrayList<>();
    private AssociationCalculationService associationCalculationService = new AssociationCalculationService();

    private XSSFSheet sheet;
    private Logger log = LoggerFactory.getLogger(getClass());
    private String logMessage;

    protected Logger getLog() {
        return log;
    }

    // Constructor
    public AssociationSheetProcessor(XSSFSheet sheet) {
        this.sheet = sheet;
        logMessage = "";

        // Read through sheet and extract values
        readSnpAssociations();
    }

    // Read and parse uploaded spreadsheet
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

                // Strings that store first 3 columns
                String authorReportedGene;
                String strongestAllele;
                String snp;

                // Get gene values
                if (row.getCell(0, row.RETURN_BLANK_AS_NULL) != null) {
                    authorReportedGene = row.getCell(0).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Gene' in row " + rowNum + 1 + "\n";

                } else {
                    authorReportedGene = null;
                    getLog().debug("Gene is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Gene' in row " + rowNum + 1 + "\n";
                }

                // Get Strongest SNP-Risk Allele
                if (row.getCell(1, row.RETURN_BLANK_AS_NULL) != null) {
                    strongestAllele = row.getCell(1).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Risk allele' in row " + rowNum + 1 + "\n";

                } else {
                    strongestAllele = null;
                    getLog().debug("Risk allele is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk allele' in row " + rowNum + 1 + "\n";
                }


                // Get SNP
                if (row.getCell(2, row.RETURN_BLANK_AS_NULL) != null) {
                    snp = row.getCell(2).getRichStringCellValue().getString();

                    logMessage = "Error in field 'SNP' in row " + rowNum + 1 + "\n";

                } else {
                    snp = null;
                    getLog().debug("SNP is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP' in row " + rowNum + 1 + "\n";

                }

                // Get Risk Allele Frequency
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

                // Get P-value mantissa	and P-value exponent
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

                // Get P-value (Text)
                String pvalueText;
                if (row.getCell(6, row.RETURN_BLANK_AS_NULL) != null) {
                    pvalueText = row.getCell(6).getRichStringCellValue().getString();
                    logMessage = "Error in field 'pvaluetxt' in row " + rowNum + 1 + "\n";

                } else {
                    pvalueText = null;
                    getLog().debug("pvalue text is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvaluetxt' in row " + rowNum + 1 + "\n";
                }

                // Get OR per copy or beta (Num)
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

                // Get OR entered (reciprocal)
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



                String orType;
                if (row.getCell(9, row.RETURN_BLANK_AS_NULL) != null) {
                    orType = row.getCell(9).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR type' in row " + rowNum + 1 + "\n";
                } else {
                    orType = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR type' in row " + rowNum + 1 + "\n";
                }

                // Get Multi-SNP Haplotype
                String multiSnpHaplotype;
                if (row.getCell(10, row.RETURN_BLANK_AS_NULL) != null) {
                    multiSnpHaplotype = row.getCell(10).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Multi-SNP Haplotype' in row " + rowNum + 1 + "\n";
                } else {
                    multiSnpHaplotype = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Multi-SNP Haplotype' in row " + rowNum + 1 + "\n";
                }

                // Get Confidence Interval/Range
                String orPerCopyRange;
                if (row.getCell(11, row.RETURN_BLANK_AS_NULL) != null) {
                    orPerCopyRange = row.getCell(11).getRichStringCellValue().getString();
                    logMessage = "Error in field 'CI' in row " + rowNum + 1 + "\n";
                } else {
                    orPerCopyRange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                    logMessage = "Error in field 'CI' in row " + rowNum + 1 + "\n";
                }

                // Get Beta unit and direction/description
                String orPerCopyUnitDescr;
                if (row.getCell(12) != null) {
                    orPerCopyUnitDescr = row.getCell(12).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR direction' in row " + rowNum + 1 + "\n";
                } else {
                    orPerCopyUnitDescr = null;
                    getLog().debug("OR direction is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR direction' in row " + rowNum + 1 + "\n";
                }

                // Get standard error
                Float orPerCopyStdError = null;
                if (row.getCell(13, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell std = row.getCell(13);
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

                // Get SNP type (novel / known)
                String snpType;
                if (row.getCell(14, row.RETURN_BLANK_AS_NULL) != null) {
                    snpType = row.getCell(14).getRichStringCellValue().getString();
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                } else {
                    snpType = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP type' in row " + rowNum + 1 + "\n";
                }


                if (authorReportedGene == null && strongestAllele == null && snp == null && riskFrequency == null) {
                    done = true;
                    getLog().debug("Empty row that wasn't caught via 'row = null'");
                } else {
                    // Create a new form which will be passed back to controller and handled there
                    SnpAssociationForm snpAssociationForm = new SnpAssociationForm();
                    snpAssociationForm.setRiskFrequency(riskFrequency);
                    snpAssociationForm.setPvalueText(pvalueText);
                    snpAssociationForm.setOrPerCopyNum(orPerCopyNum);


                    if (orType.equalsIgnoreCase("Y")) {
                        snpAssociationForm.setOrType(true);
                    } else {
                        snpAssociationForm.setOrType(false);
                    }

                    if (multiSnpHaplotype.equalsIgnoreCase("Y")) {
                        snpAssociationForm.setMultiSnpHaplotype(true);
                    } else {
                        snpAssociationForm.setMultiSnpHaplotype(false);
                    }

                    snpAssociationForm.setSnpType(snpType);
                    snpAssociationForm.setPvalueMantissa(pvalueMantissa);
                    snpAssociationForm.setPvalueExponent(pvalueExponent);
                    snpAssociationForm.setOrPerCopyRecip(orPerCopyRecip);
                    snpAssociationForm.setOrPerCopyStdError(orPerCopyStdError);
                    snpAssociationForm.setOrPerCopyRange(orPerCopyRange);
                    snpAssociationForm.setOrPerCopyUnitDescr(orPerCopyUnitDescr);

                    // For our list of snps and risk alleles separate by comma
                    List<String> snps = new ArrayList<>();
                    String[] separatedSnps = snp.split(",");
                    for (String separatedSnp : separatedSnps) {
                        snps.add(separatedSnp.trim());
                    }


                    List<String> riskAlleles = new ArrayList<>();
                    String[] separatedRiskAlleles = strongestAllele.split(",");
                    for (String separatedRiskAllele : separatedRiskAlleles) {
                        riskAlleles.add(separatedRiskAllele.trim());
                    }

                    // Create row from each SNP/Risk allele combination
                    Iterator<String> riskAlleleIterator = riskAlleles.iterator();
                    Iterator<String> snpIterator = snps.iterator();
                    List<SnpFormRow> snpFormRows = new ArrayList<>();

                    // Lopp through our risk alleles and snps
                    if (riskAlleles.size() == snps.size()) {

                        while (riskAlleleIterator.hasNext()) {
                            // Create a new row
                            SnpFormRow snpFormRow = new SnpFormRow();
                            String snpValue = snpIterator.next().trim();
                            String riskAlleleValue = riskAlleleIterator.next().trim();

                            snpFormRow.setSnp(snpValue);
                            snpFormRow.setStrongestRiskAllele(riskAlleleValue);
                            snpFormRows.add(snpFormRow);
                        }

                    } else {
                        // TODO THIS SHOULD DIE CLEANLY
                        getLog().error("Mismatched number of snps and risk alleles");
                    }

                    // Add rows to our form
                    snpAssociationForm.setSnpFormRows(snpFormRows);

                    // Handle curator entered genes
                    String[] genes = authorReportedGene.split(",");
                    Collection<String> authorReportedGenes = new ArrayList<>();

                    for (String gene : genes) {
                        gene.trim();
                        authorReportedGenes.add(gene);
                    }
                    snpAssociationForm.setAuthorReportedGenes(authorReportedGenes);

                    // Add all newly created associations to collection
                    snpAssociationForms.add(snpAssociationForm);
                }

            }
            rowNum++;
        }
    }

    public Collection<SnpAssociationForm> getAllSnpAssociationForms() {
        return snpAssociationForms;
    }

    public String getLogMessage() {
        return logMessage;
    }


}
