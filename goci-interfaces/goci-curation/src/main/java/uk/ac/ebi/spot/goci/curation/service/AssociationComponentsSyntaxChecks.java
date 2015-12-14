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

    // Check for common errors in snp and risk allele names
    public String checkSnpOrProxy(String snpValue) {

        String error = "";
        if (snpValue.contains(",")) {
            error = "SNP " + snpValue + " contains a ',' character.";
        }
        if (snpValue.contains("x")) {
            error = error + "SNP " + snpValue + " contains an 'x' character.";
        }
        if (snpValue.contains("X")) {
            error = error + "SNP " + snpValue + " contains an 'X' character.";
        }
        if (snpValue.contains(":")) {
            error = error + "SNP " + snpValue + " contains a ':' character.";
        }
        if (snpValue.contains(";")) {
            error = error + "SNP " + snpValue + " contains a ';' character.";
        }
        if (snpValue.contains("-")) {
            error = error + "SNP " + snpValue + " contains a '-' character.";
        }
        if (!snpValue.startsWith("rs")) {
            error = error + "SNP " + snpValue + " does not start with rs.";
        }

        return error;
    }

    public String checkRiskAllele(String riskAllele) {

        String error = "";
        if (riskAllele.contains(",")) {
            error = "Risk Allele " + riskAllele + " contains a ',' character.";
        }
        if (riskAllele.contains("x")) {
            error = error + "Risk Allele " + riskAllele + " contains an 'x' character.";
        }
        if (riskAllele.contains("X")) {
            error = error + "Risk Allele " + riskAllele + " contains an 'X' character.";
        }
        if (riskAllele.contains(":")) {
            error = error + "Risk Allele " + riskAllele + " contains a ':' character.";
        }
        if (riskAllele.contains(";")) {
            error = error + "Risk Allele " + riskAllele + " contains a ';' character.";
        }
        if (!riskAllele.startsWith("rs")) {
            error = error + "Risk Allele " + riskAllele + " does not start with rs.";
        }

        return error;
    }

}
