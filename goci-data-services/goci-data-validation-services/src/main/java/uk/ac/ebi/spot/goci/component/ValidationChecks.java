package uk.ac.ebi.spot.goci.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.service.rest.GeneCheckingRestService;
import uk.ac.ebi.spot.goci.service.rest.SnpCheckingRestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by emma on 01/04/2016.
 *
 * @author emma
 *         <p>
 *         Contains a list of common validation checks
 */
@Component
public class ValidationChecks {

    private GeneCheckingRestService geneCheckingRestService;

    private SnpCheckingRestService snpCheckingRestService;

    @Autowired
    public ValidationChecks(GeneCheckingRestService geneCheckingRestService,
                            SnpCheckingRestService snpCheckingRestService) {
        this.geneCheckingRestService = geneCheckingRestService;
        this.snpCheckingRestService = snpCheckingRestService;
    }

    /**
     * Check value is populated
     *
     * @param value Value to be check presence
     */
    public String checkValueIsPresent(String value) {
        String error = null;

        if (value == null) {
            error = "Value is empty";
        }
        else {
            if (value.isEmpty()) {
                error = "Value is empty";
            }
        }
        return error;
    }

    /**
     * Check value is empty
     *
     * @param value Value to be checked
     */
    public String checkValueIsEmpty(String value) {
        String error = null;

        if (value != null && !value.isEmpty()) {
            error = "Value is not empty";
        }
        return error;
    }

    /**
     * Check value is empty
     *
     * @param value Value to be checked
     */
    public String checkValueIsEmpty(Float value) {
        String error = null;

        if (value != null) {
            error = "Value is not empty";
        }
        return error;
    }

    /**
     * Check snp type contains only values 'novel' or 'known'
     *
     * @param value Value to be checked
     */
    public String checkSnpType(String value) {
        String error = null;

        if (value != null) {
            switch (value) {
                case "novel":
                    break;
                case "known":
                    break;
                default:
                    error = "Value does not contain novel or known";
            }
        }
        else {
            error = "Value is empty";
        }
        return error;
    }

    /**
     * OR MUST be filled and more than 1
     *
     * @param value Value to be checked
     */
    public String checkOrIsPresentAndMoreThanOne(Float value) {
        String error = null;

        if (value == null) {
            error = "Value is empty";
        }
        else {
            if (value < 1) {
                error = "Value is less than 1";
            }
        }
        return error;
    }

    /**
     * Beta MUST be filled
     *
     * @param value Value to be checked
     */
    public String checkBetaIsPresentAndIsNotNegative(Float value) {
        String error = null;

        if (value == null) {
            error = "Value is empty";
        }
        else {
            if (value < 0) {
                error = "Value is less than 0";
            }
        }
        return error;
    }

    /**
     * "Beta direction" MUST be filled
     *
     * @param value Value to be checked
     */
    public String checkBetaDirectionIsPresent(String value) {
        String error = null;

        if (value == null) {
            error = "Value is empty";
        }
        else {
            switch (value) {
                case "increase":
                    break;
                case "decrease":
                    break;
                default:
                    error = "Value is not increase or decrease";
            }
        }
        return error;
    }

    /**
     * P-value mantissa check number of digits.
     *
     * @param value Value to be checked
     */
    public String checkMantissaIsLessThan10(Integer value) {
        String error = null;

        if (value != null) {
            if (value > 9) {
                error = "Value not valid i.e. greater than 9";
            }
        }
        else {
            error = "Value is empty";
        }
        return error;
    }

    /**
     * P-value exponent check
     *
     * @param value Value to be checked
     */
    public String checkExponentIsPresent(Integer value) {
        String error = null;

        if (value == null) {
            error = "Value is empty";
        }
        else {
            if (value == 0) {
                error = "Value is zero";
            }
        }
        return error;
    }

    /**
     * Gene check
     *
     * @param geneName Gene name to be checked
     */
    public String checkGene(String geneName) {
        String error = null;
        if (geneName == null) {
            error = "Gene name is empty";
        }
        else {
            if (geneName.isEmpty()) {
                error = "Gene name is empty";
            }
            // Check gene name in Ensembl
            else {
                error = geneCheckingRestService.checkGeneSymbolIsValid(geneName);
            }
        }
        return error;
    }

    /**
     * Snp check
     *
     * @param snp Snp identifier to be checked
     */
    public String checkSnp(String snp) {
        String error = null;
        if (snp == null) {
            error = "SNP identifier is empty";
        }
        else {
            if (snp.isEmpty()) {
                error = "SNP identifier is empty";
            }
            // Check SNP in Ensembl
            else {
                error = snpCheckingRestService.checkSnpIdentifierIsValid(snp);
            }
        }
        return error;
    }

    /**
     * Gene and SNP not on same Chr
     *
     * @param snp  Snp identifier to be checked
     * @param gene Gene name to be checked
     */
    public String checkSnpGeneLocation(String snp, String gene) {
        String error = null;

        // Ensure valid gene and snp
        String snpError = checkSnp(snp);

        if (snpError != null) {
            error = snpError;
        }
        else {
            String geneError = checkGene(gene);

            if (geneError != null) {
                error = geneError;
            }
            else {
                // Get all SNP locations and check gene location is one of them
                Set<String> snpChromosomeNames = snpCheckingRestService.getSnpLocations(snp);
                if (!snpChromosomeNames.isEmpty()) {
                    String geneChromosome = geneCheckingRestService.getGeneLocation(gene);
                    if (!snpChromosomeNames.contains(geneChromosome)) {
                        error = "Gene ".concat(gene)
                                .concat(" and SNP ")
                                .concat(snp)
                                .concat(" are not on same chromosome");
                    }
                }
                else {
                    error = "SNP ".concat(snp)
                            .concat(" has no location details, cannot check if gene is on same chromosome as SNP");
                }
            }
        }
        return error;
    }


    /**
     * Risk allele check
     *
     * @param riskAlleleName to be checked
     */
    public String checkRiskAllele(String riskAlleleName) {
        String error = null;
        List<String> acceptableValues = new ArrayList<>();
        acceptableValues.add("A");
        acceptableValues.add("T");
        acceptableValues.add("G");
        acceptableValues.add("C");
        acceptableValues.add("?");

        if (riskAlleleName == null) {
            error = "Risk allele is empty";
        }
        else {
            if (riskAlleleName.isEmpty()) {
                error = "Risk allele is empty";
            }
            // Check risk allele is one of the accepted types
            else {
                if (!acceptableValues.contains(riskAlleleName)) {
                    error = "Risk allele is not one of ".concat(acceptableValues.toString());
                }
            }
        }
        return error;
    }


    /**
     * Risk frequency check
     *
     * @param riskFrequency Risk frequency value to be checked
     */
    public String checkRiskFrequency(String riskFrequency) {
        String error = null;
        if (riskFrequency == null) {
            error = "Empty value";
        }
        else if (riskFrequency.isEmpty()) {
            error = "Empty value";
        }
        else {
            // Skip check if value is NR
            if (!riskFrequency.equals("NR")) {

                try {
                    float f = Float.parseFloat(riskFrequency);
                    // if string contains only numbers then check its value is between valid range
                    if (f < 0 || f > 1) {
                        error = "Value is invalid, value is not between 0 and 1";
                    }
                }
                catch (NumberFormatException e) {
                    if (!riskFrequency.contentEquals("NR")) {
                        error = "Value is invalid i.e. not equal to NR or a number";
                    }
                }
            }
        }
        return error;
    }

    /**
     * Check value contains the required delimiter
     *
     * @param value     Value to check
     * @param delimiter
     */
    public String checkSynthax(String value, String delimiter) {
        String error = null;
        if (!value.contains(delimiter)) {
            error = "Value does not contain correct separator";
        }
        return error;
    }

    /**
     * Check that SNP status attributes are set to true for at least one
     *
     * @param genomeWide
     * @param limitedList
     */
    public String checkSnpStatus(Boolean genomeWide, Boolean limitedList) {
        String error = null;
        if (!genomeWide && !limitedList) {
            error = "No status selected";
        }
        return error;
    }
}