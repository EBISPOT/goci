package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.AssociationFormErrorView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by emma on 29/05/2015.
 *
 * @author emma
 *         <p>
 *         Service class that determines possible errors in curator entered fields. Also generates list of errors and
 *         their types from mapping pipeline.
 */
@Service
public class AssociationFormErrorViewService {

    private AssociationMappingErrorService associationMappingErrorService;

    @Autowired
    public AssociationFormErrorViewService(AssociationMappingErrorService associationMappingErrorService) {
        this.associationMappingErrorService = associationMappingErrorService;
    }

    /**
     * Determine error prone attributes of association and then return them via controller to view
     *
     * @param association Association object
     */
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

                    if (riskAllele.getRiskAlleleName() != null) {
                        associationRiskAlleles.add(riskAllele.getRiskAlleleName());
                    }
                    if (riskAllele.getSnp().getRsId() != null) {
                        associationSnps.add(riskAllele.getSnp().getRsId());
                    }
                    if (riskAllele.getProxySnps() != null) {
                        for (SingleNucleotidePolymorphism proxySnp : riskAllele.getProxySnps()) {
                            associationProxies.add(proxySnp.getRsId());
                        }
                    }
                }
            }
        }

        String error = "";

        // Risk allele errors
        for (String riskAlleleName : associationRiskAlleles) {
            error = checkRiskAllele(riskAlleleName);
            if (!error.isEmpty()) {
                riskAlleleErrors.add(error);
            }
        }

        //SNP errors
        for (String snpName : associationSnps) {
            error = checkSnpOrProxy(snpName);
            if (!error.isEmpty()) {
                snpErrors.add(error);
            }
        }

        // Proxy errors
        for (String proxyName : associationProxies) {
            error = checkSnpOrProxy(proxyName);
            if (!error.isEmpty()) {
                proxyErrors.add(error);
            }
        }

        // Check association report for errors from mapping pipeline
        Map<String, String> associationErrorMap =
                associationMappingErrorService.createAssociationErrorMap(association.getAssociationReport());

        // Set model attributes
        associationErrorView.setRiskAlleleErrors(formatErrors(riskAlleleErrors));
        associationErrorView.setSnpErrors(formatErrors(snpErrors));
        associationErrorView.setProxyErrors(formatErrors(proxyErrors));
        associationErrorView.setAssociationErrorMap(associationErrorMap);
        return associationErrorView;
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

    public String formatErrors(Collection<String> errors) {
        String error = "";
        error = String.join(" ", errors);
        return error;
    }
}
