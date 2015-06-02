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

    public AssociationViewService() {
    }

    public SnpAssociationTableView createSnpAssociationTableView(Association association) {
        SnpAssociationTableView snpAssociationTableView = new SnpAssociationTableView();

        // SNP interaction studies should be separated by an 'x'
        String delimiter = ", ";
        if (association.getSnpInteraction()) {
            delimiter = " x ";
        }

        snpAssociationTableView.setAssociationId(association.getId());

        // For each locus get genes, risk alleles, snps, proxy snps
        Collection<Locus> loci = association.getLoci();
        Collection<String> locusGenes = new ArrayList<>();
        Collection<String> locusRiskAlleles = new ArrayList<String>();
        Collection<String> snps = new ArrayList<String>();
        Collection<String> proxySnps = new ArrayList<String>();
        Collection<String> regions = new ArrayList<String>();
        Collection<String> riskAlleleFrequencies = new ArrayList<String>();
        Collection<String> snpStatuses = new ArrayList<String>();

        // By looking at each locus we can keep order in view
        for (Locus locus : loci) {

            // Store gene names
            // A locus can have a number of genes attached
            // Per locus create a comma separated list and add to an array
            // Further processing will then delimit this list
            // either by comma or 'x' depending on association type
            Collection<String> currentlocusGenes = new ArrayList<>();
            String commaSeparatedGenes = "";
            for (Gene gene : locus.getAuthorReportedGenes()) {
                currentlocusGenes.add(gene.getGeneName());
            }
            commaSeparatedGenes = String.join(", ", currentlocusGenes);
            locusGenes.add(commaSeparatedGenes);

            for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                locusRiskAlleles.add(riskAllele.getRiskAlleleName());
                SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                snps.add(snp.getRsId());

                // TODO CHANGE WHEN WE UPDATE MODEL FOR MULTIPLE PROXY SNPS
                // Set proxy if one is present
                if (riskAllele.getProxySnp() != null) {
                    proxySnps.add(riskAllele.getProxySnp().getRsId());
                }

                // Store region information
                if (snp.getRegions() != null && !snp.getRegions().isEmpty()) {
                    for (Region region : snp.getRegions()) {
                        regions.add(region.getName());
                    }
                }

                // Allele risk frequency
                if (riskAllele.getRiskFrequency() != null && !riskAllele.getRiskFrequency().isEmpty()) {
                    riskAlleleFrequencies.add(riskAllele.getRiskFrequency());
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
                    snpStatuses.add(commaSeparatedSnpStatus);
                }
            }
        }

        String associationRegions = null;
        associationRegions = String.join(delimiter, regions);
        snpAssociationTableView.setRegions(associationRegions);

        String authorReportedGenes = null;
        authorReportedGenes = String.join(delimiter, locusGenes);
        snpAssociationTableView.setAuthorReportedGenes(authorReportedGenes);

        String strongestRiskAlleles = null;
        strongestRiskAlleles = String.join(delimiter, locusRiskAlleles);
        snpAssociationTableView.setStrongestRiskAlleles(strongestRiskAlleles);

        String associationSnps = null;
        associationSnps = String.join(delimiter, snps);
        snpAssociationTableView.setSnps(associationSnps);

        String associationProxies = null;
        associationProxies = String.join(delimiter, proxySnps);
        snpAssociationTableView.setProxySnps(associationProxies);

        // Set both risk frequencies
        String associationRiskAlleleFrequencies = null;
        associationRiskAlleleFrequencies = String.join(delimiter, riskAlleleFrequencies);
        snpAssociationTableView.setRiskAlleleFrequencies(associationRiskAlleleFrequencies);
        snpAssociationTableView.setAssociationRiskFrequency(association.getRiskFrequency());

        String associationSnpStatuses = null;
        associationSnpStatuses = String.join(delimiter, snpStatuses);
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
        snpAssociationTableView.setSnpTypes(association.getSnpType());


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
