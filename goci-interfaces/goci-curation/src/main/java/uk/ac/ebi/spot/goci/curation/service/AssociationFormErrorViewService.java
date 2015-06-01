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

        String riskAlleleErrors = "Risk Allele errors: ";
        String snpErrors = "SNP errors: ";
        String proxyErrors = "Proxy errors: ";

        for (Locus locus : association.getLoci()) {

            if (locus != null) {
                for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                    associationRiskAlleles.add(riskAllele.getRiskAlleleName());
                    associationSnps.add(riskAllele.getSnp().getRsId());
                    associationProxies.add(riskAllele.getProxySnp().getRsId());

                }
            }
        }
        String error = "";

        // Risk allele errors
        for (String riskAlleleName : associationRiskAlleles) {
            error = error + checkSnpOrRiskAllele(riskAlleleName);
        }
        if (!error.isEmpty()) {
            riskAlleleErrors = riskAlleleErrors + error;
        }
        else {
            riskAlleleErrors = riskAlleleErrors + "No errors\n";
        }

        //SNP errors
        error = "";
        for (String snpName : associationSnps) {
            error = error + checkSnpOrRiskAllele(snpName);
        }
        if (!error.isEmpty()) {
            snpErrors = snpErrors + error;
        }
        else {
            snpErrors = snpErrors + "No errors\n";
        }

        // Proxy errors
        error = "";
        for (String proxyName : associationProxies) {
            error = error + checkSnpOrRiskAllele(proxyName);
        }
        if (!error.isEmpty()) {
            proxyErrors = proxyErrors + error;
        }
        else { proxyErrors = proxyErrors + "No errors\n";}

        // Set model attributes
        associationErrorView.setRiskAlleleErrors(riskAlleleErrors);
        associationErrorView.setSnpErrors(snpErrors);
        associationErrorView.setProxyErrors(proxyErrors);
        return associationErrorView;
    }

    // Check for common errors in sno and risk allele names
    public String checkSnpOrRiskAllele(String snpValue) {

        String error = "";
        if (snpValue.contains(",")) {
            error = "SNP " + snpValue + " contains a ',' character\n";
        }
        if (snpValue.contains("x")) {
            error = error + "SNP " + snpValue + " contains an x character\n";
        }
        if (snpValue.contains("X")) {
            error = error + "SNP " + snpValue + " contains an X character\n";
        }
        if (snpValue.contains(":")) {
            error = error + "SNP " + snpValue + " contains a ':' character\n";
        }
        if (snpValue.contains(";")) {
            error = error + "SNP " + snpValue + " contains a ';' character\n";
        }
        if (!snpValue.startsWith("rs")) {
            error = error + "SNP " + snpValue + " does not start with rs\n";
        }

        return error;
    }
}
