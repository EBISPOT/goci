package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.utils.AssociationCalculationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 *         <p>
 *         Service to create an association from each row of an uploaded spreadsheet, which can then be passed to the
 *         validator.
 */
@Service
public class AssociationRowProcessor {

    private AssociationAttributeService associationAttributeService;

    private AssociationCalculationService associationCalculationService;

    @Autowired
    public AssociationRowProcessor(AssociationAttributeService associationAttributeService,
                                   AssociationCalculationService associationCalculationService) {
        this.associationAttributeService = associationAttributeService;
        this.associationCalculationService = associationCalculationService;
    }

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public Association createAssociationFromUploadRow(AssociationUploadRow row) {

        Association newAssociation = new Association();

        // Set EFO traits
        if (row.getEfoTrait() != null) {
            String[] uris = row.getEfoTrait().split(",");
            Collection<String> efoUris = new ArrayList<>();

            for (String uri : uris) {
                String trimmedUri = uri.trim();
                efoUris.add(trimmedUri);
            }

            Collection<EfoTrait> efoTraits = associationAttributeService.getEfoTraitsFromRepository(efoUris);
            newAssociation.setEfoTraits(efoTraits);
        }

        /// Set OR
        newAssociation.setOrPerCopyRecip(row.getOrPerCopyRecip());
        newAssociation.setOrPerCopyRecipRange(row.getOrPerCopyRecipRange());

        // Set beta
        newAssociation.setBetaNum(row.getBetaNum());
        newAssociation.setBetaUnit(row.getBetaUnit());
        newAssociation.setBetaDirection(row.getBetaDirection());

        // Calculate OR num if OR recip is present , otherwise set to whatever is in upload
        boolean recipReverse = false;
        if ((row.getOrPerCopyRecip() != null) && (row.getOrPerCopyNum() == null)) {
            getLog().info("Calculating OR from OR recip value");
            newAssociation.setOrPerCopyNum(((100 / row.getOrPerCopyRecip()) / 100));
            recipReverse = true;
        }
        else {
            newAssociation.setOrPerCopyNum(row.getOrPerCopyNum());
        }

        // Calculate range
        // (This logic is retained from Dani's original code)
        if ((row.getOrPerCopyRecipRange() != null) && recipReverse) {
            newAssociation.setRange(associationCalculationService.reverseCI(row.getOrPerCopyRecipRange()));
        }
        else if ((row.getRange() == null) && (row.getStandardError() != null)) {

            if (row.getOrPerCopyNum() != null) {
                newAssociation.setRange(associationCalculationService.setRange(row.getStandardError(),
                                                                               row.getOrPerCopyNum()));
            }
            else {
                if (row.getBetaNum() != null) {
                    newAssociation.setRange(associationCalculationService.setRange(row.getStandardError(),
                                                                                   row.getBetaNum()));
                }
            }
        }
        else {
            newAssociation.setRange(row.getRange());
        }

        // Set values common to all association types
        newAssociation.setRiskFrequency(row.getAssociationRiskFrequency());
        newAssociation.setPvalueMantissa(row.getPvalueMantissa());
        newAssociation.setPvalueExponent(row.getPvalueExponent());
        newAssociation.setPvalueDescription(row.getPvalueDescription());
        newAssociation.setSnpType(row.getSnpType());
        newAssociation.setStandardError(row.getStandardError());
        newAssociation.setDescription(row.getDescription());

        if (row.getMultiSnpHaplotype() != null) {
            if (row.getMultiSnpHaplotype().equalsIgnoreCase("Y")) {
                newAssociation.setMultiSnpHaplotype(true);
            }
        }
        else {
            newAssociation.setMultiSnpHaplotype(false);
        }

        if (row.getSnpInteraction() != null) {
            if (row.getSnpInteraction().equalsIgnoreCase("Y")) {
                newAssociation.setSnpInteraction(true);
            }
        }
        else {
            newAssociation.setSnpInteraction(false);
        }

        String delimiter;
        Collection<Locus> loci = new ArrayList<>();

        if (newAssociation.getSnpInteraction()) {
            delimiter = "x";

            // For SNP interaction studies we need to create a locus per risk allele
            // Handle curator entered risk allele
            Collection<RiskAllele> locusRiskAlleles =
                    createLocusRiskAlleles(row.getStrongestAllele(),
                                           row.getSnp(),
                                           row.getProxy(),
                                           row.getRiskFrequency(),
                                           row.getSnpStatus(),
                                           delimiter);

            // Deal with genes for each interaction which should be
            // separated by 'x'
            String[] separatedGenes = row.getAuthorReportedGene().split(delimiter);
            int geneIndex = 0;

            for (RiskAllele riskAllele : locusRiskAlleles) {
                Locus locus = new Locus();

                // Set risk alleles, assume one locus per risk allele
                Collection<RiskAllele> currentLocusRiskAlleles = new ArrayList<>();
                currentLocusRiskAlleles.add(riskAllele);
                locus.setStrongestRiskAlleles(currentLocusRiskAlleles);

                // Set gene
                String interactionGene = separatedGenes[geneIndex];
                Collection<Gene> locusGenes = associationAttributeService.createLocusGenes(interactionGene, ",");
                locus.setAuthorReportedGenes(locusGenes);
                geneIndex++;

                // Set description
                locus.setDescription("SNP x SNP interaction");
                loci.add(locus);
            }
        }

        // Handle multi-snp and standard snp
        else {
            delimiter = ";";

            // For multi-snp and standard snps we assume their is only one locus
            Locus locus = new Locus();

            // Handle curator entered genes, for haplotype they are separated by a comma
            Collection<Gene> locusGenes =
                    associationAttributeService.createLocusGenes(row.getAuthorReportedGene(), ",");
            locus.setAuthorReportedGenes(locusGenes);

            // Handle curator entered risk allele
            Collection<RiskAllele> locusRiskAlleles =
                    createLocusRiskAlleles(row.getStrongestAllele(),
                                           row.getSnp(),
                                           row.getProxy(),
                                           row.getRiskFrequency(),
                                           row.getSnpStatus(),
                                           delimiter);


            // For standard associations set the risk allele frequency to the
            // same value as the overall association frequency
            Collection<RiskAllele> locusRiskAllelesWithRiskFrequencyValues = new ArrayList<>();
            if (!newAssociation.getMultiSnpHaplotype()) {
                for (RiskAllele riskAllele : locusRiskAlleles) {
                    riskAllele.setRiskFrequency(row.getAssociationRiskFrequency());
                    locusRiskAllelesWithRiskFrequencyValues.add(riskAllele);
                }
                locus.setStrongestRiskAlleles(locusRiskAllelesWithRiskFrequencyValues);
            }

            else {
                locus.setStrongestRiskAlleles(locusRiskAlleles);
            }

            // Set locus attributes
            Integer haplotypeCount = locusRiskAlleles.size();
            if (haplotypeCount > 1) {
                locus.setHaplotypeSnpCount(haplotypeCount);
                locus.setDescription(String.valueOf(haplotypeCount) + "-SNP haplotype");
            }

            else {
                locus.setDescription("Single variant");
            }

            loci.add(locus);
        }

        newAssociation.setLoci(loci);
        return newAssociation;
    }

    private Collection<RiskAllele> createLocusRiskAlleles(String strongestAllele,
                                                          String snp,
                                                          String proxy,
                                                          String riskFrequency,
                                                          String snpStatus,
                                                          String delimiter) {


        Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();
        // For our list of snps, proxies and risk alleles separate by delimiter
        List<String> snps = new ArrayList<>();
        String[] separatedSnps = snp.split(delimiter);
        for (String separatedSnp : separatedSnps) {
            snps.add(separatedSnp.trim());
        }

        List<String> riskAlleles = new ArrayList<>();
        String[] separatedRiskAlleles = strongestAllele.split(delimiter);
        for (String separatedRiskAllele : separatedRiskAlleles) {
            riskAlleles.add(separatedRiskAllele.trim());
        }

        List<String> proxies = new ArrayList<>();
        if (proxy != null) {
            String[] separatedProxies = proxy.split(delimiter);
            for (String separatedProxy : separatedProxies) {
                proxies.add(separatedProxy.trim());
            }
        }

        // Value is only recorded for SNP interaction associations
        List<String> riskFrequencies = new ArrayList<>();
        Iterator<String> riskFrequencyIterator = null;
        if (riskFrequency != null) {
            String[] separatedRiskFrequencies = riskFrequency.split(delimiter);
            for (String separatedRiskFrequency : separatedRiskFrequencies) {
                riskFrequencies.add(separatedRiskFrequency.trim());
            }
            riskFrequencyIterator = riskFrequencies.iterator();
        }

        // Snp status
        List<String> snpStatuses = new ArrayList<>();
        Iterator<String> snpStatusIterator = null;
        if (snpStatus != null) {
            String[] separatedSnpStatuses = snpStatus.split(delimiter);
            for (String separatedSnpStatus : separatedSnpStatuses) {
                snpStatuses.add(separatedSnpStatus.trim());
            }
            snpStatusIterator = snpStatuses.iterator();
        }

        Iterator<String> riskAlleleIterator = riskAlleles.iterator();
        Iterator<String> snpIterator = snps.iterator();
        Iterator<String> proxyIterator = proxies.iterator();

        // Loop through our risk alleles
        if (riskAlleles.size() == snps.size()) {

            while (riskAlleleIterator.hasNext()) {

                String snpValue = snpIterator.next().trim();
                String riskAlleleValue = riskAlleleIterator.next().trim();

                SingleNucleotidePolymorphism newSnp = associationAttributeService.createSnp(snpValue);

                // Create a new risk allele and assign newly created snp
                RiskAllele newRiskAllele = associationAttributeService.createRiskAllele(riskAlleleValue, newSnp);

                // Check for proxies and if we have one create a proxy snp
                if (proxies.size() != 0) {
                    if (proxies.size() ==snps.size()){

                        String proxyValue = proxyIterator.next().trim();

                        Collection<SingleNucleotidePolymorphism> newRiskAlleleProxies = new ArrayList<>();
                        if (proxyValue.contains(":")) {
                            String[] splitProxyValues = proxyValue.split(":");

                            for (String splitProxyValue : splitProxyValues) {
                                SingleNucleotidePolymorphism proxySnp =
                                        associationAttributeService.createSnp(splitProxyValue.trim());
                                newRiskAlleleProxies.add(proxySnp);
                            }
                        }

                        else if (proxyValue.contains(",")) {
                            String[] splitProxyValues = proxyValue.split(",");

                            for (String splitProxyValue : splitProxyValues) {
                                SingleNucleotidePolymorphism proxySnp =
                                        associationAttributeService.createSnp(splitProxyValue.trim());
                                newRiskAlleleProxies.add(proxySnp);
                            }
                        }

                        else {
                            SingleNucleotidePolymorphism proxySnp = associationAttributeService.createSnp(proxyValue);
                            newRiskAlleleProxies.add(proxySnp);
                        }
                        newRiskAllele.setProxySnps(newRiskAlleleProxies);
                    }
                    else{
                        getLog().error("Proxy SNP number and SNP number do not match");
                    }
                }

                // If there is no curator entered value for risk allele frequency don't save
                String riskFrequencyValue = null;
                if (riskFrequencyIterator != null) {
                    riskFrequencyValue = riskFrequencyIterator.next().trim();
                }
                if (riskFrequencyValue != null) {
                    newRiskAllele.setRiskFrequency(riskFrequencyValue);
                }

                // Handle snp statuses, these should only apply to SNP interaction associations
                String snpStatusValue = null;
                if (snpStatusIterator != null) {
                    snpStatusValue = snpStatusIterator.next().trim();
                }

                if (snpStatus != null && !snpStatus.equalsIgnoreCase("NR")) {
                    if (snpStatusValue.contains("GW") || snpStatusValue.contains("gw")) {
                        newRiskAllele.setGenomeWide(true);
                    }
                    if (snpStatusValue.contains("LL") || snpStatusValue.contains("ll")) {
                        newRiskAllele.setLimitedList(true);
                    }
                }

                locusRiskAlleles.add(newRiskAllele);
            }
        }
        else {
            getLog().error("Mismatched number of snps and risk alleles");
        }

        return locusRiskAlleles;
    }
}