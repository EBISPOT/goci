package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationTableView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 20/05/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates table view of a study's associations
 */
@Service
public class SnpAssociationTableViewService {

    private AssociationMappingErrorService associationMappingErrorService;
    private AssociationValidationReportService associationValidationReportService;

    @Autowired
    public SnpAssociationTableViewService(AssociationMappingErrorService associationMappingErrorService,
                                          AssociationValidationReportService associationValidationReportService) {
        this.associationMappingErrorService = associationMappingErrorService;
        this.associationValidationReportService = associationValidationReportService;
    }

    /**
     * Create object, from Association, that will be returned to view
     *
     * @param association Association object
     */
    public SnpAssociationTableView createSnpAssociationTableView(Association association) {
        SnpAssociationTableView snpAssociationTableView = new SnpAssociationTableView();

        // For SNP interaction studies snp, proxy snps, risk alleles etc
        // should be separated by an 'x'
        String delimiter = "; ";
        if (association.getSnpInteraction()) {
            delimiter = " x ";
        }

        snpAssociationTableView.setAssociationId(association.getId());

        // For each locus relevant attributes
        Collection<Locus> loci = association.getLoci();
        Collection<String> allLociGenes = new ArrayList<>();
        Collection<String> allLociRiskAlleles = new ArrayList<String>();
        Collection<String> allLociSnps = new ArrayList<String>();
        Collection<String> allLociProxySnps = new ArrayList<String>();
        Collection<String> allLociRiskAlleleFrequencies = new ArrayList<String>();
        Collection<String> allLociSnpStatuses = new ArrayList<String>();

        // By looking at each locus in turn we can keep order in view
        for (Locus locus : loci) {

            // Store gene names, a locus can have a number of genes attached.
            // Per locus create a comma separated list and add to an array.
            // Further processing will then delimit this list
            // either by ; or 'x' depending on association type
            Collection<String> currentlocusGenes = new ArrayList<>();
            String commaSeparatedGenes = "";
            for (Gene gene : locus.getAuthorReportedGenes()) {
                currentlocusGenes.add(gene.getGeneName());
            }
            if (!currentlocusGenes.isEmpty()) {
                commaSeparatedGenes = String.join(", ", currentlocusGenes);
                allLociGenes.add(commaSeparatedGenes);
            }
            else { allLociGenes.add("NR"); }

            for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                allLociRiskAlleles.add(riskAllele.getRiskAlleleName());

                // For standard association set the risk allele frequency
                // Based on assumption we only have one locus with a single risk allele attached
                if (!association.getMultiSnpHaplotype() && !association.getSnpInteraction()) {
                    if (riskAllele.getRiskFrequency() != null && !riskAllele.getRiskFrequency().isEmpty()) {
                        allLociRiskAlleleFrequencies.add(riskAllele.getRiskFrequency());
                    }
                }

                // SNPs attached to risk allele
                SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                allLociSnps.add(snp.getRsId());

                // Set proxies if present
                Collection<String> currentLocusProxies = new ArrayList<>();
                String commaSeparatedProxies = "";
                if (riskAllele.getProxySnps() != null) {
                    for (SingleNucleotidePolymorphism proxySnp : riskAllele.getProxySnps()) {
                        currentLocusProxies.add(proxySnp.getRsId());
                    }
                }

                // Comma separate proxies in view
                if (!currentLocusProxies.isEmpty()) {
                    commaSeparatedProxies = String.join(", ", currentLocusProxies);
                    allLociProxySnps.add(commaSeparatedProxies);
                }

                else { allLociProxySnps.add("NR");}

                // Only required for SNP interaction studies
                if (association.getSnpInteraction() != null) {
                    if (association.getSnpInteraction()) {

                        // Genome wide Vs Limited List
                        Collection<String> snpStatus = new ArrayList<>();
                        String commaSeparatedSnpStatus = "";
                        if (riskAllele.getLimitedList() != null) {
                            if (riskAllele.getLimitedList()) {
                                snpStatus.add("LL");
                            }
                        }
                        if (riskAllele.getGenomeWide() != null) {
                            if (riskAllele.getGenomeWide()) {
                                snpStatus.add("GW");
                            }
                        }
                        if (!snpStatus.isEmpty()) {
                            commaSeparatedSnpStatus = String.join(", ", snpStatus);
                            allLociSnpStatuses.add(commaSeparatedSnpStatus);
                        }
                        else { allLociSnpStatuses.add("NR");}


                        // Allele risk frequency
                        if (riskAllele.getRiskFrequency() != null && !riskAllele.getRiskFrequency().isEmpty()) {
                            allLociRiskAlleleFrequencies.add(riskAllele.getRiskFrequency());
                        }
                        else {
                            allLociRiskAlleleFrequencies.add("NR");
                        }
                    }
                }
            }
        }

        // Create delimited strings for view
        String authorReportedGenes = null;
        if (allLociGenes.size() > 1) {
            authorReportedGenes = String.join(delimiter, allLociGenes);
        }
        else {
            authorReportedGenes = String.join("", allLociGenes);
        }
        snpAssociationTableView.setAuthorReportedGenes(authorReportedGenes);

        String strongestRiskAlleles = null;
        if (allLociRiskAlleles.size() > 1) {
            strongestRiskAlleles = String.join(delimiter, allLociRiskAlleles);
        }
        else {
            strongestRiskAlleles = String.join("", allLociRiskAlleles);
        }
        snpAssociationTableView.setStrongestRiskAlleles(strongestRiskAlleles);

        String associationSnps = null;
        if (allLociSnps.size() > 1) {
            associationSnps = String.join(delimiter, allLociSnps);
        }
        else {
            associationSnps = String.join("", allLociSnps);
        }
        snpAssociationTableView.setSnps(associationSnps);

        String associationProxies = null;
        if (allLociProxySnps.size() > 1) {
            associationProxies = String.join(delimiter, allLociProxySnps);
        }
        else {
            associationProxies = String.join("", allLociProxySnps);
        }
        snpAssociationTableView.setProxySnps(associationProxies);

        // Set both risk frequencies
        String associationRiskAlleleFrequencies = null;
        if (allLociRiskAlleleFrequencies.size() > 1) {
            associationRiskAlleleFrequencies = String.join(delimiter, allLociRiskAlleleFrequencies);
        }
        else {
            associationRiskAlleleFrequencies = String.join("", allLociRiskAlleleFrequencies);
        }
        snpAssociationTableView.setRiskAlleleFrequencies(associationRiskAlleleFrequencies);
        snpAssociationTableView.setAssociationRiskFrequency(association.getRiskFrequency());

        String associationSnpStatuses = null;
        if (allLociSnpStatuses.size() > 1) {
            associationSnpStatuses = String.join(delimiter, allLociSnpStatuses);
        }
        else {
            associationSnpStatuses = String.join("", allLociSnpStatuses);
        }
        snpAssociationTableView.setSnpStatuses(associationSnpStatuses);

        snpAssociationTableView.setPvalueMantissa(association.getPvalueMantissa());
        snpAssociationTableView.setPvalueExponent(association.getPvalueExponent());
        snpAssociationTableView.setPvalueDescription(association.getPvalueDescription());


        Collection<String> efoTraits = new ArrayList<>();
        for (EfoTrait efoTrait : association.getEfoTraits()) {
            efoTraits.add(efoTrait.getTrait());

        }
        String associationEfoTraits = null;
        associationEfoTraits = String.join(", ", efoTraits);
        snpAssociationTableView.setEfoTraits(associationEfoTraits);

        // Set OR values
        snpAssociationTableView.setOrPerCopyNum(association.getOrPerCopyNum());
        snpAssociationTableView.setOrPerCopyRecip(association.getOrPerCopyRecip());
        snpAssociationTableView.setOrPerCopyRecipRange(association.getOrPerCopyRecipRange());

        // Set beta values
        snpAssociationTableView.setBetaNum(association.getBetaNum());
        snpAssociationTableView.setBetaDirection(association.getBetaDirection());
        snpAssociationTableView.setBetaUnit(association.getBetaUnit());

        snpAssociationTableView.setRange(association.getRange());
        snpAssociationTableView.setDescription(association.getDescription());
        snpAssociationTableView.setStandardError(association.getStandardError());
        snpAssociationTableView.setAssociationType(association.getSnpType());


        if (association.getMultiSnpHaplotype() != null) {
            if (association.getMultiSnpHaplotype()) {
                snpAssociationTableView.setMultiSnpHaplotype("Yes");
            }

            if (!association.getMultiSnpHaplotype()) {
                snpAssociationTableView.setMultiSnpHaplotype("No");
            }
        }

        if (association.getSnpInteraction() != null) {
            if (association.getSnpInteraction()) {
                snpAssociationTableView.setSnpInteraction("Yes");
            }

            if (!association.getSnpInteraction()) {
                snpAssociationTableView.setSnpInteraction("No");
            }
        }

        if (association.getSnpApproved() != null) {
            if (association.getSnpApproved()) {
                snpAssociationTableView.setSnpApproved("Yes");
            }


            if (!association.getSnpApproved()) {
                snpAssociationTableView.setSnpApproved("No");
            }
        }

        // Check if the errors in the association report have been checked by a curator
        if (association.getAssociationReport() != null) {
            if (association.getAssociationReport().getErrorCheckedByCurator() != null) {
                if (association.getAssociationReport().getErrorCheckedByCurator()) {
                    snpAssociationTableView.setAssociationErrorsChecked("Yes");
                }

                if (!association.getAssociationReport().getErrorCheckedByCurator()) {
                    snpAssociationTableView.setAssociationErrorsChecked("No");
                }
            }
        }

        // Check for mapping errors
        snpAssociationTableView.setAssociationErrorMap(associationMappingErrorService.createAssociationErrorMap(
                association.getAssociationReport()));

       // Check for validation warnings
        long associationValidationReportCount =
                associationValidationReportService.getAssociationWarnings(association.getId()).stream().count();
        if (associationValidationReportCount > 0) {
            snpAssociationTableView.setValidationWarnings(true);
        }
        else {snpAssociationTableView.setValidationWarnings(false);}

        // Get mapping details
        if (association.getLastMappingPerformedBy() != null) {
            snpAssociationTableView.setLastMappingPerformedBy(association.getLastMappingPerformedBy());
        }

        if (association.getLastMappingDate() != null) {
            DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String dateOfLastMapping = df.format(association.getLastMappingDate());
            snpAssociationTableView.setLastMappingDate(dateOfLastMapping);
        }

        return snpAssociationTableView;
    }
}
