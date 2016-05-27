package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;

/**
 * Created by emma on 14/12/2015.
 *
 * @author emma
 *         <p>
 *         Performs basic syntax checking of main association components
 */
@Service
public class AssociationComponentsSyntaxChecks {

    public AssociationComponentsSyntaxChecks() {
    }

    /**
     * Check value for common syntax errors
     *
     * @param snpValue SNP RS_ID
     */
    public String checkSnp(String snpValue) {

        String error = "";
        error = runCommonChecks(snpValue, "SNP");

        if (snpValue.contains("-")) {
            error = error + "SNP " + snpValue + " contains a '-' character. ";
        }
        if (!snpValue.startsWith("rs")) {
            error = error + "SNP " + snpValue + " does not start with rs. ";
        }

        return error;
    }

    /**
     * Check value for common syntax errors
     *
     * @param snpValue Proxy SNP RS_ID
     */
    public String checkProxy(String snpValue) {

        String error = "";
        error = runCommonChecks(snpValue, "Proxy SNP");

        if (snpValue.contains("-")) {
            error = error + "SNP " + snpValue + " contains a '-' character. ";
        }
        if (!snpValue.equals("NR")) {
            if (!snpValue.startsWith("rs")) {
                error = error + "SNP " + snpValue + " does not start with rs. ";
            }
        }

        return error;
    }

    /**
     * Check value for common syntax errors
     *
     * @param riskAllele Risk allele name
     */
    public String checkRiskAllele(String riskAllele) {

        String error = "";
        error = runCommonChecks(riskAllele, "Risk Allele");

        if (!riskAllele.startsWith("rs")) {
            error = error + "Risk Allele " + riskAllele + " does not start with rs. ";
        }

        return error;
    }

    /**
     * Check value for common syntax errors
     *
     * @param value     value to check
     * @param valueType Type of value i.e. SNP, Proxy SNP, Risk Allele
     */

    public String runCommonChecks(String value, String valueType) {

        String error = "";
        if (value.contains(",")) {
            error = valueType + " " + value + " contains a ',' character. ";
        }
        if (value.contains("x")) {
            error = error + valueType + " " + value + " contains an 'x' character. ";
        }
        if (value.contains("X")) {
            error = error + valueType + " " + value + " contains an 'X' character. ";
        }
        if (value.contains(":")) {
            error = error + valueType + " " + value + " contains a ':' character. ";
        }
        if (value.contains(";")) {
            error = error + valueType + " " + value + " contains a ';' character. ";
        }

        return error;
    }

}
