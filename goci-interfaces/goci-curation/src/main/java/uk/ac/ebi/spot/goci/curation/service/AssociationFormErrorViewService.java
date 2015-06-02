package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.AssociationFormErrorView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 29/05/2015.
 *
 * @author emma
 *         <p>
 *         Service class that determines possible errors in curator entered fields
 */
@Service
public class AssociationFormErrorViewService {

    public AssociationFormErrorViewService() {
    }

    // Determine error prone attributes of association and then return them via controller to view
    public AssociationFormErrorView checkAssociationForErrors(Association association) {

        AssociationFormErrorView associationErrorView = new AssociationFormErrorView();
        Collection<String> associationRiskAlleles = new ArrayList<String>();
        Collection<String> associationSnps = new ArrayList<String>();
        Collection<String> associationProxies = new ArrayList<String>();

        Collection<String> riskAlleleErrors = new ArrayList<String>();
        Collection<String> snpErrors = new ArrayList<String>();
        Collection<String> proxyErrors = new ArrayList<String>();

        // Store attributes of each loci
        for (Locus locus : association.getLoci()) {

            if (locus != null) {
                for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                    associationRiskAlleles.add(riskAllele.getRiskAlleleName());
                    associationSnps.add(riskAllele.getSnp().getRsId());

                    if (riskAllele.getProxySnp() != null) {
                        associationProxies.add(riskAllele.getProxySnp().getRsId());
                    }
                }
            }
        }

        String error = "";

        // Risk allele errors
        for (String riskAlleleName : associationRiskAlleles) {
            error = checkSnpOrRiskAllele(riskAlleleName);
            if (!error.isEmpty()) {
                riskAlleleErrors.add(error);
            }
        }

        //SNP errors
        for (String snpName : associationSnps) {
            error = checkSnpOrRiskAllele(snpName);
            if (!error.isEmpty()) {
                snpErrors.add(error);
            }
        }

        // Proxy errors
        for (String proxyName : associationProxies) {
            error = checkSnpOrRiskAllele(proxyName);
            if (!error.isEmpty()) {
                proxyErrors.add(error);
            }
        }

        // Set model attributes
        associationErrorView.setRiskAlleleErrors(formatErrors(riskAlleleErrors));
        associationErrorView.setSnpErrors(formatErrors(snpErrors));
        associationErrorView.setProxyErrors(formatErrors(proxyErrors));
        return associationErrorView;
    }

    // Check for common errors in snp and risk allele names
    public String checkSnpOrRiskAllele(String snpValue) {

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
            error = error + "SNP " + snpValue + " contains a ';' character." ;
        }
        if (!snpValue.startsWith("rs")) {
            error = error + "SNP " + snpValue + " does not start with rs.";
        }

        return error;
    }

    public String formatErrors(Collection<String> errors){
        String error = "";
        error = String.join(" ", errors);
        return error;
    }
}
