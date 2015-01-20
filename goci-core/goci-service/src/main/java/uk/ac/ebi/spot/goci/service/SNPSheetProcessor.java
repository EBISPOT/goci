package uk.ac.ebi.spot.goci.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;


/**
 * @author dwelter
 *         <p>
 *         This classes takes an Excel spreadsheet sheet and extracts all the SNP records
 *         For each SNP, a SNPentry object is created
 */

public class SNPSheetProcessor {

    private XSSFSheet sheet;
    private ArrayList<Association> allSNPs;
    private Logger log = LoggerFactory.getLogger(getClass());
    private String logMessage;

    protected Logger getLog() {
        return log;
    }

    public SNPSheetProcessor(XSSFSheet sheet) {

        this.sheet = sheet;

        allSNPs = new ArrayList<Association>();
        logMessage = "";
        readSNPs();
    }

    public void readSNPs() {
        boolean done = false;
        int rownum = 1;

        while (!done) {
            XSSFRow row = sheet.getRow(rownum);

            if (row == null) {
                done = true;
                getLog().debug("Last row read");
                logMessage = "All spreadsheet data processed successfully";
            } else {
                String authorReportedGene, strongestAllele, snp;
                Collection<SingleNucleotidePolymorphism> snps = new ArrayList<>();

                if (row.getCell(0, row.RETURN_BLANK_AS_NULL) != null) {
                    authorReportedGene = row.getCell(0).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Gene' in row " + rownum + 1 + "\n";

                } else {
                    authorReportedGene = null;
                    getLog().debug("Gene is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Gene' in row " + rownum + 1 + "\n";
                }

                if (row.getCell(1, row.RETURN_BLANK_AS_NULL) != null) {
                    strongestAllele = row.getCell(1).getRichStringCellValue().getString();
                    logMessage = "Error in field 'Risk allele' in row " + rownum + 1 + "\n";

                } else {
                    strongestAllele = null;
                    getLog().debug("Risk allele is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk allele' in row " + rownum + 1 + "\n";
                }

                if (row.getCell(2, row.RETURN_BLANK_AS_NULL) != null) {
                    snp = row.getCell(2).getRichStringCellValue().getString();

                    // More than one SNP entered in row, user should separate these with comma
                    if (snp.contains(",")) {
                        String[] splitSnpStrings = snp.split(",");
                        for (String splitSnpString : splitSnpStrings) {
                            SingleNucleotidePolymorphism newSnp = new SingleNucleotidePolymorphism();
                            newSnp.setRsId(splitSnpString);
                            snps.add(newSnp);
                        }
                    }

                    // Single snp entered
                    else {
                        SingleNucleotidePolymorphism newSnp = new SingleNucleotidePolymorphism();
                        newSnp.setRsId(snp);
                        snps.add(newSnp);
                    }
                    logMessage = "Error in field 'SNP' in row " + rownum + 1 + "\n";

                } else {
                    snp = null;
                    getLog().debug("SNP is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP' in row " + rownum + 1 + "\n";

                }

                String riskFrequency = null;

                if (row.getCell(3, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell risk = row.getCell(3);
                    switch (risk.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            riskFrequency = risk.getRichStringCellValue().getString();
                            logMessage = "Error in field 'Risk Frequency' in row " + rownum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            riskFrequency = Double.toString(risk.getNumericCellValue());
                            logMessage = "Error in field 'Risk Frequency' in row " + rownum + 1 + "\n";

                            break;
                    }
                } else {
                    getLog().debug("RF is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Risk Frequency' in row " + rownum + 1 + "\n";
                }

                Integer pvalueMantissa = null;
                Integer pvalueExponent = null;

                if (row.getCell(4, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell mant = row.getCell(4);
                    switch (mant.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueMantissa = null;
                            logMessage = "Error in field 'pvalue mantissa' in row " + rownum + 1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueMantissa = (int) mant.getNumericCellValue();
                            logMessage = "Error in field 'pvalue mantissa' in row " + rownum + 1 + "\n";

                            break;
                    }
                } else {
                    pvalueMantissa = null;
                    getLog().debug("pvalue mantissa is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue mantissa' in row " + rownum + 1 + "\n";

                }

                if (row.getCell(5, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell expo = row.getCell(5);
                    switch (expo.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            pvalueExponent = null;
                            logMessage = "Error in field 'pvalue exponent' in row " + rownum + 1 + "\n";

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            pvalueExponent = (int) expo.getNumericCellValue();
                            logMessage = "Error in field 'pvalue exponent' in row " + rownum + 1 + "\n";

                            break;
                    }
                } else {
                    pvalueExponent = null;
                    getLog().debug("pvalue exponent is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvalue exponent' in row " + rownum + 1 + "\n";
                }

                Float pvalueFloat;

                if (pvalueExponent != null && pvalueMantissa != null) {
                    double calculatedPvalueFloat = (pvalueMantissa * Math.pow(10, pvalueExponent));
                    pvalueFloat = Float.valueOf((float) calculatedPvalueFloat);
                } else {
                    pvalueFloat = Float.valueOf(0);
                }


                String pvalueText;
                if (row.getCell(6, row.RETURN_BLANK_AS_NULL) != null) {
                    pvalueText = row.getCell(6).getRichStringCellValue().getString();
                    logMessage = "Error in field 'pvaluetxt' in row " + rownum + 1 + "\n";

                } else {
                    pvalueText = null;
                    getLog().debug("pvalue text is null in row " + row.getRowNum());
                    logMessage = "Error in field 'pvaluetxt' in row " + rownum + 1 + "\n";
                }

                Float orPerCopyNum = null;
                if (row.getCell(7, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell or = row.getCell(7);
                    switch (or.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyNum = null;
                            logMessage = "Error in field 'OR' in row " + rownum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyNum = Float.valueOf((float) or.getNumericCellValue());
                            logMessage = "Error in field 'OR' in row " + rownum + 1 + "\n";
                            break;
                    }
                } else {
                    orPerCopyNum = null;
                    getLog().debug("OR is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR' in row " + rownum + 1 + "\n";
                }

                Float orPerCopyRecip = null;
                if (row.getCell(8, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell recip = row.getCell(8);
                    switch (recip.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyRecip = null;
                            logMessage = "Error in field 'OR recip' in row " + rownum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyRecip = Float.valueOf((float) recip.getNumericCellValue());
                            logMessage = "Error in field 'OR recip' in row " + rownum + 1 + "\n";
                            break;
                    }
                } else {
                    orPerCopyRecip = null;
                    getLog().debug("OR recip is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR recip' in row " + rownum + 1 + "\n";

                }

           /*     TODO WHAT IS THIS

                boolean recipReverse = false;

                if ((orpercopyrecip != null) && (orpercopynum == null)) {
                    orpercopynum = ((100 / orpercopyrecip) / 100);
                    recipReverse = true;
                }*/

                String orType;
                if (row.getCell(9, row.RETURN_BLANK_AS_NULL) != null) {
                    orType = row.getCell(9).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR type' in row " + rownum + 1 + "\n";
                } else {
                    orType = null;
                    getLog().debug("OR type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR type' in row " + rownum + 1 + "\n";
                }

                String orPerCopyRange;
                if (row.getCell(10, row.RETURN_BLANK_AS_NULL) != null) {
                    orPerCopyRange = row.getCell(10).getRichStringCellValue().getString();
                    logMessage = "Error in field 'CI' in row " + rownum + 1 + "\n";
                } else {
                    orPerCopyRange = null;
                    getLog().debug("CI is null in row " + row.getRowNum());
                    logMessage = "Error in field 'CI' in row " + rownum + 1 + "\n";
                }

                String orPerCopyUnitDescr;
                if (row.getCell(11) != null) {
                    orPerCopyUnitDescr = row.getCell(11).getRichStringCellValue().getString();
                    logMessage = "Error in field 'OR direction' in row " + rownum + 1 + "\n";
                } else {
                    orPerCopyUnitDescr = null;
                    getLog().debug("OR direction is null in row " + row.getRowNum());
                    logMessage = "Error in field 'OR direction' in row " + rownum + 1 + "\n";
                }

                Float orPerCopyStdError = null;

                if (row.getCell(12, row.RETURN_BLANK_AS_NULL) != null) {
                    XSSFCell std = row.getCell(12);
                    switch (std.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            orPerCopyStdError = null;
                            logMessage = "Error in field 'Standard Error' in row " + rownum + 1 + "\n";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            orPerCopyStdError = Float.valueOf((float) std.getNumericCellValue());
                            logMessage = "Error in field 'Standard Error' in row " + rownum + 1 + "\n";
                            break;
                    }
                } else {
                    orPerCopyStdError = null;
                    getLog().debug("SE is null in row " + row.getRowNum());
                    logMessage = "Error in field 'Standard Error' in row " + rownum + 1 + "\n";
                }

                /* TODO WHATS THIS DOING
                if ((orpercopyrecip != null) && (orpercopyrange != null) && recipReverse) {
                    orpercopyrange = reverseCI(orpercopyrange);
                }

                if ((orpercopyrange == null) && (orpercopystderror != null)) {
                    orpercopyrange = setRange(orpercopystderror, orpercopynum);
                }*/

                String snpType;
                if (row.getCell(13, row.RETURN_BLANK_AS_NULL) != null) {
                    snpType = row.getCell(13).getRichStringCellValue().getString();
                    logMessage = "Error in field 'SNP type' in row " + rownum + 1 + "\n";
                } else {
                    snpType = null;
                    getLog().debug("SNP type is null in row " + row.getRowNum());
                    logMessage = "Error in field 'SNP type' in row " + rownum + 1 + "\n";
                }


                if (authorReportedGene == null && strongestAllele == null && snp == null && riskFrequency == null) {
                    done = true;
                    getLog().debug("Empty row that wasn't caught via 'row = null'");
                } else {
                    Association thisSNP;
                    thisSNP = new Association();
                    thisSNP.setAuthorReportedGene(authorReportedGene);
                    thisSNP.setStrongestAllele(strongestAllele);
                    thisSNP.setRiskFrequency(riskFrequency);
                    thisSNP.setPvalueFloat(pvalueFloat);
                    thisSNP.setPvalueText(pvalueText);
                    thisSNP.setOrPerCopyNum(orPerCopyNum);
                    thisSNP.setOrType(orType);
                    thisSNP.setSnpType(snpType);
                    thisSNP.setPvalueMantissa(pvalueMantissa);
                    thisSNP.setPvalueExponent(pvalueExponent);
                    thisSNP.setOrPerCopyRecip(orPerCopyRecip);
                    thisSNP.setOrPerCopyStdError(orPerCopyStdError);
                    thisSNP.setOrPerCopyRange(orPerCopyRange);
                    thisSNP.setOrPerCopyUnitDescr(orPerCopyUnitDescr);

                    // Add all newly created associations to collection
                    allSNPs.add(thisSNP);
                }

            }
            rownum++;
        }
    }


    /**
     * This method calculates the confidence interval based on the standard error - formatting code taken from Kent's Coldfusion code.
     *
     * @param orpc_stderr The standard error
     * @param orpc_num    The odds-ratio or beta value for the SNP
     * @return String The confidence interval for the odds-ratio or beta value
     */
    public String setRange(double orpc_stderr, double orpc_num) {
        double delta = (100000 * orpc_stderr * 1.96) / 100000;
        double low = orpc_num - delta;
        double high = orpc_num + delta;
        String lowval, highval;

        if (low < 0.001) {
            DecimalFormat df = new DecimalFormat("#.#####");
            lowval = df.format(low);
            highval = df.format(high);
        } else if (low >= 0.001 && low < 0.01) {
            DecimalFormat df = new DecimalFormat("#.####");
            lowval = df.format(low);
            highval = df.format(high);
        } else if (low >= 0.01 && low < 0.1) {
            DecimalFormat df = new DecimalFormat("#.###");
            lowval = df.format(low);
            highval = df.format(high);
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            lowval = df.format(low);
            highval = df.format(high);
        }

        String orpc_range = ("[" + lowval + "-" + highval + "]");

        return orpc_range;
    }

    /**
     * This method reverses the confidence interval for SNPs where the reciprocal odds-ratio was provided.
     *
     * @param interval The confidence interval
     * @return String The reversed confidence interval
     */
    public String reverseCI(String interval) {
        String newInterval;
        String ci = interval.replace("[", "");
        ci = ci.replace("]", "");

        if (ci.equals("NR")) {
            newInterval = interval;
        } else if (ci.matches("[\\d\\s.-]+")) {
            String[] num = ci.split("-");
            double one = Double.parseDouble(num[0].trim());
            double two = Double.parseDouble(num[1].trim());

            double high = ((100 / one) / 100);
            double low = ((100 / two) / 100);

            String lowval, highval;

            if (low < 0.001) {
                DecimalFormat df = new DecimalFormat("#.#####");
                lowval = df.format(low);
                highval = df.format(high);
            } else if (low >= 0.001 && low < 0.01) {
                DecimalFormat df = new DecimalFormat("#.####");
                lowval = df.format(low);
                highval = df.format(high);
            } else if (low >= 0.01 && low < 0.1) {
                DecimalFormat df = new DecimalFormat("#.###");
                lowval = df.format(low);
                highval = df.format(high);
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                lowval = df.format(low);
                highval = df.format(high);
            }

            newInterval = ("[" + lowval + "-" + highval + "]");
        } else {
            newInterval = null;
            getLog().info("Unprocessable character in the CI");
        }

        return newInterval;
    }

    public ArrayList<Association> getSNPlist() {
        return allSNPs;
    }

    public String getLogMessage() {
        return logMessage;
    }
}
