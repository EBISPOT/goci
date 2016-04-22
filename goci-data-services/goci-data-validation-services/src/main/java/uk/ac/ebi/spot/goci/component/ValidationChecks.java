package uk.ac.ebi.spot.goci.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emma on 01/04/2016.
 *
 * @author emma
 *         <p>
 *         Contains a list of common validation checks
 */
@Component
public class ValidationChecks {

    private GeneValidationChecks geneValidationChecks;

    @Autowired
    public ValidationChecks(GeneValidationChecks geneValidationChecks) {
        this.geneValidationChecks = geneValidationChecks;
    }

    /**
     * Check value is populated
     *
     * @param value Value to be checked
     */
    public String checkValueIsPresent(String value) {
        String error = null;

        if (value == null) {
            error = "Empty value";

        }
        else {
            if (value.isEmpty()) {
                error = "Empty value";
            }

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
                    error = "SNP type does not contain novel or known";
            }
        }
        else {
            error = "SNP type is empty";
        }
        return error;
    }

    /**
     * OR MUST be filled and less than 1
     *
     * @param value Value to be checked
     */
    public String checkOrIsPresentAndLessThanOne(Float value) {
        String error = null;

        if (value == null) {
            error = "OR num is empty for association";
        }
        else {
            if (value > 1) {
                error = "OR num is more than 1 for association";
            }
        }
        return error;
    }

    /**
     * "Beta" columns MUST be empty
     *
     * @param value Value to be checked
     */
    public String checkBetaValueIsEmpty(Float value) {

        String error = null;

        if (value != null) {
            error = ("Beta value found");
        }
        return error;
    }

    /**
     * "Beta unit" columns MUST be empty
     *
     * @param value Value to be checked
     */
    public String checkBetaUnitIsEmpty(String value) {
        String error = null;

        if (value != null) {
            error = "Beta unit found for association";
        }
        return error;
    }

    /**
     * "Beta direction" columns MUST be empty
     *
     * @param value Value to be checked
     */
    public String checkBetaDirectionIsEmpty(String value) {
        String error = null;

        if (value != null) {
            error = "Beta direction found for association";
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
            error = "Beta is empty";
        }
        else {
            if (value < 0) {
                error = "Beta is less than 0";
            }
        }
        return error;
    }

    /**
     * "Beta unit" MUST be filled
     *
     * @param value Value to be checked
     */
    public String checkBetaUnitIsPresent(String value) {
        String error = null;

        if (value == null) {
            error = "Beta unit is empty for association";
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
            error = "Beta direction is empty for association";
        }
        else {
            switch (value) {
                case "increase":
                    break;
                case "decrease":
                    break;
                default:
                    error = "Beta direction is not increase or decrease for association";
            }
        }
        return error;
    }

    /**
     * "OR" MUST be empty.
     *
     * @param value Value to be checked
     */
    public String checkOrEmpty(Float value) {
        String error = null;

        if (value != null) {
            error = "OR num found for association";
        }
        return error;
    }

    /**
     * "Reciprocal OR" MUST be empty.
     *
     * @param value Value to be checked
     */
    public String checkOrRecipEmpty(Float value) {
        String error = null;

        if (value != null) {
            error = "OR reciprocal found for association";
        }
        return error;
    }

    /**
     * "OR reciprocal range" MUST be empty.
     *
     * @param value Value to be checked
     */
    public String checkOrPerCopyRecipRangeIsEmpty(String value) {
        String error = null;

        if (value != null) {
            error = "OR reciprocal range found for association";
        }
        return error;
    }

    /**
     * Range MUST be empty.
     *
     * @param value Value to be checked
     */
    public String checkRangeIsEmpty(String value) {

        String error = null;

        if (value != null) {
            error = "Range found for association";
        }
        return error;
    }

    /**
     * "Standard Error" MUST be empty.
     *
     * @param value Value to be checked
     */
    public String checkStandardErrorIsEmpty(Float value) {

        String error = null;
        if (value != null) {
            error = "Standard error found for association";
        }
        return error;
    }

    /**
     * "Description" MUST be empty.
     *
     * @param value Value to be checked
     */
    public String checkDescriptionIsEmpty(String value) {
        String error = null;

        if (value != null) {
            error = "OR/Beta description found for association";
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
                error = "P-value mantisaa not valid";
            }
        }
        else {
            error = "P-value mantissa is empty";
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
            error = "P-value exponent is empty";
        }
        else {
            if (value == 0) {
                error = "P-value exponent is zero";
            }
        }
        return error;
    }

    /**
     * Gene  check
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
                error = geneValidationChecks.checkGeneSymbolIsValid(geneName);
            }
        }
        return error;
    }

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
            // Check  risk allele is one of the accepted types
            else {
                if (!acceptableValues.contains(riskAlleleName)) {
                    error = "Risk allele is not one of ".concat(acceptableValues.toString());
                }
            }
        }
        return error;
    }
}