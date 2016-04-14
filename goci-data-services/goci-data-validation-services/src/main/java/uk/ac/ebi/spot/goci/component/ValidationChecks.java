package uk.ac.ebi.spot.goci.component;

import org.springframework.stereotype.Component;

/**
 * Created by emma on 01/04/2016.
 *
 * @author emma
 *         <p>
 *         Contains a list of common validation checks
 */
@Component
public class ValidationChecks {

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
     * "OR" MUST be filled
     *
     * @param value Value to be checked
     */
    public String checkOrIsPresent(Float value) {
        String error = null;

        if (value == null) {
            error = "OR num is empty for association";
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
     * "Beta" MUST be filled
     *
     * @param value Value to be checked
     */
    public String checkBetaIsPresent(Float value) {
        String error = null;

        if (value == null) {
            error = "Beta is empty for association";
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
     * "Range" MUST be empty.
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
     * "P-value mantissa" check number of digits.
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
}