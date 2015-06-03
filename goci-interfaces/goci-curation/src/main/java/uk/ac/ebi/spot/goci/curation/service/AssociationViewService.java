package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationTableView;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 20/05/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates table view of a studies associations
 */
@Service
public class AssociationViewService {

    // Constructor
    public AssociationViewService() {
    }

    // Create object that will be returned to view
    public SnpAssociationTableView createSnpAssociationTableView(Association association) {
        SnpAssociationTableView snpAssociationTableView = new SnpAssociationTableView();

        // For SNP interaction studies snp, proxy snps, risk alleles etc
        // should be separated by an 'x'
        String delimiter = ", ";
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
        Collection<String> allLociRegions = new ArrayList<String>();
        Collection<String> allLociRiskAlleleFrequencies = new ArrayList<String>();
        Collection<String> allLociSnpStatuses = new ArrayList<String>();

        // By looking at each locus in turn we can keep order in view
        for (Locus locus : loci) {

            // Store gene names
            // A locus can have a number of genes attached
            // Per locus create a comma separated list and add to an array.
            // Further processing will then delimit this list
            // either by comma or 'x' depending on association type
            Collection<String> currentlocusGenes = new ArrayList<>();
            String commaSeparatedGenes = "";
            for (Gene gene : locus.getAuthorReportedGenes()) {
                currentlocusGenes.add(gene.getGeneName());
            }
            if (!currentlocusGenes.isEmpty()) {
                commaSeparatedGenes = String.join(", ", currentlocusGenes);
                allLociGenes.add(commaSeparatedGenes);
            }
            else { allLociGenes.add("NA"); }

            for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                allLociRiskAlleles.add(riskAllele.getRiskAlleleName());

                // SNPs attached to risk allele
                SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                allLociSnps.add(snp.getRsId());

                // TODO CHANGE WHEN WE UPDATE MODEL FOR MULTIPLE PROXY SNPS
                // Set proxy if one is present
                if (riskAllele.getProxySnp() != null) {
                    allLociProxySnps.add(riskAllele.getProxySnp().getRsId());
                }
                else { allLociProxySnps.add("NA");}

                // Store region information
                Collection<String> currentLocusSnpRegions = new ArrayList<>();
                String commaSeparatedRegions = "";
                if (snp.getRegions() != null && !snp.getRegions().isEmpty()) {
                    for (Region region : snp.getRegions()) {
                        currentLocusSnpRegions.add(region.getName());
                    }
                }
                if (!currentLocusSnpRegions.isEmpty()) {
                    commaSeparatedRegions = String.join(", ", currentLocusSnpRegions);
                    allLociRegions.add(commaSeparatedRegions);
                }

                // These are only required for SNP interaction studies
                if (association.getSnpInteraction() != null) {
                    if (association.getSnpInteraction()) {

                        // Allele risk frequency
                        if (riskAllele.getRiskFrequency() != null && !riskAllele.getRiskFrequency().isEmpty()) {
                            allLociRiskAlleleFrequencies.add(riskAllele.getRiskFrequency());
                        }
                        else {
                            allLociRiskAlleleFrequencies.add("NA");
                        }

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
                        else { allLociSnpStatuses.add("NA");}
                    }
                }
            }
        }

        // Create delimited strings for view
        String associationRegions = null;
        if (allLociRegions.size() > 1) {
            associationRegions = String.join(delimiter, allLociRegions);
        }
        else {
            associationRegions = String.join("", allLociRegions);
        }
        snpAssociationTableView.setRegions(associationRegions);

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
        snpAssociationTableView.setPvalueText(association.getPvalueText());


        Collection<String> efoTraits = new ArrayList<>();
        for (EfoTrait efoTrait : association.getEfoTraits()) {
            efoTraits.add(efoTrait.getTrait());

        }
        String associationEfoTraits = null;
        associationEfoTraits = String.join(", ", efoTraits);
        snpAssociationTableView.setEfoTraits(associationEfoTraits);

        snpAssociationTableView.setOrPerCopyNum(association.getOrPerCopyNum());
        snpAssociationTableView.setOrPerCopyRecip(association.getOrPerCopyRecip());

        if (association.getOrType() != null) {
            if (association.getOrType()) {
                snpAssociationTableView.setOrType("Yes");
            }

            if (!association.getOrType()) {
                snpAssociationTableView.setOrType("No");
            }
        }

        snpAssociationTableView.setOrPerCopyRange(association.getOrPerCopyRange());
        snpAssociationTableView.setOrPerCopyRecipRange(association.getOrPerCopyRecipRange());
        snpAssociationTableView.setOrPerCopyUnitDescr(association.getOrPerCopyUnitDescr());
        snpAssociationTableView.setOrPerCopyStdError(association.getOrPerCopyStdError());
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

        if (association.getSnpChecked() != null) {
            if (association.getSnpChecked()) {
                snpAssociationTableView.setSnpChecked("Yes");
            }


            if (!association.getSnpChecked()) {
                snpAssociationTableView.setSnpChecked("No");
            }
        }

        // Set error map
        snpAssociationTableView.setAssociationErrorMap(createAssociationErrorMap(association.getAssociationReport()));
        return snpAssociationTableView;
    }


    private Map<String, String> createAssociationErrorMap(AssociationReport associationReport) {

        Map<String, String> associationErrorMap = new HashMap<>();

        //Create map of errors
        if (associationReport != null) {
            if (associationReport.getSnpError() != null && !associationReport.getSnpError().isEmpty()) {
                associationErrorMap.put("SNP Error: ", associationReport.getSnpError());
            }

            if (associationReport.getGeneNotOnGenome() != null &&
                    !associationReport.getGeneNotOnGenome().isEmpty()) {
                associationErrorMap.put("Gene Not On Genome Error: ", associationReport.getGeneNotOnGenome());
            }

            if (associationReport.getSnpGeneOnDiffChr() != null &&
                    !associationReport.getSnpGeneOnDiffChr().isEmpty()) {
                associationErrorMap.put("Snp Gene On Diff Chr: ", associationReport.getSnpGeneOnDiffChr());
            }

            if (associationReport.getNoGeneForSymbol() != null &&
                    !associationReport.getNoGeneForSymbol().isEmpty()) {
                associationErrorMap.put("No Gene For Symbol: ", associationReport.getNoGeneForSymbol());
            }
        }

        return associationErrorMap;
    }
}
