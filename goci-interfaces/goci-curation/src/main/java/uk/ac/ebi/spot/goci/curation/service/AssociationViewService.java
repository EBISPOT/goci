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
 * Service class that creates table view of a studies associations
 */
@Service
public class AssociationViewService {

    public AssociationViewService() {
    }

    public SnpAssociationTableView createSnpAssociationTableView(Association association) {
        SnpAssociationTableView snpAssociationTableView = new SnpAssociationTableView();

        snpAssociationTableView.setAssociationId(association.getId());

        // For each locus get genes, risk alleles, snps, proxy snps
        Collection<Locus> loci = association.getLoci();
        Collection<String> locusGenes = new ArrayList<>();
        Collection<String> locusRiskAlleles = new ArrayList<String>();
        Collection<String> snps = new ArrayList<String>();
        Collection<String> proxySnps = new ArrayList<String>();
        Collection<String> regions = new ArrayList<String>();

        for (Locus locus : loci) {

            // Store gene names
            for (Gene gene : locus.getAuthorReportedGenes()) {
                locusGenes.add(gene.getGeneName());
            }

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
            }
        }

        String associationRegions = null;
        associationRegions = String.join(",", regions);
        snpAssociationTableView.setRegions(associationRegions);

        String authorReportedGenes = null;
        authorReportedGenes = String.join(",", locusGenes);
        snpAssociationTableView.setAuthorReportedGenes(authorReportedGenes);

        String strongestRiskAlleles = null;
        strongestRiskAlleles = String.join(",", locusRiskAlleles);
        snpAssociationTableView.setStrongestRiskAlleles(strongestRiskAlleles);

        String associationSnps = null;
        associationSnps = String.join(",", snps);
        snpAssociationTableView.setSnps(associationSnps);

        String associationProxies = null;
        associationProxies = String.join(",", proxySnps);
        snpAssociationTableView.setProxySnps(associationProxies);

        snpAssociationTableView.setRiskFrequency(association.getRiskFrequency());
        snpAssociationTableView.setPvalueMantissa(association.getPvalueMantissa());
        snpAssociationTableView.setPvalueExponent(association.getPvalueExponent());
        snpAssociationTableView.setPvalueText(association.getPvalueText());


        Collection<String> efoTraits = new ArrayList<>();
        for (EfoTrait efoTrait : association.getEfoTraits()) {
            efoTraits.add(efoTrait.getTrait());

        }
        String associationEfoTraits = null;
        associationEfoTraits = String.join(",", efoTraits);
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
        snpAssociationTableView.setSnpType(association.getSnpType());


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
