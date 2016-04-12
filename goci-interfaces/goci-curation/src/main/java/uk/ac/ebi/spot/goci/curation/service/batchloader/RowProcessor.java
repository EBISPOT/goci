package uk.ac.ebi.spot.goci.curation.service.batchloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.AssociationUploadRow;
import uk.ac.ebi.spot.goci.curation.service.LociAttributesService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by emma on 21/03/2016.
 *
 * @author emma
 */
@Service
public class RowProcessor {

    // Services
    private LociAttributesService lociAttributesService;

    // Repository
    private EfoTraitRepository efoTraitRepository;
    private LocusRepository locusRepository;

    // Logging
    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public RowProcessor(LociAttributesService lociAttributesService,
                        EfoTraitRepository efoTraitRepository,
                        LocusRepository locusRepository) {
        this.lociAttributesService = lociAttributesService;
        this.efoTraitRepository = efoTraitRepository;
        this.locusRepository = locusRepository;
    }

    public Collection<Association> createAssociationsFromUploadRows(Collection<AssociationUploadRow> rows) {

        // Create collection to store all newly created associations
        Collection<Association> newAssociations = new ArrayList<>();

        for (AssociationUploadRow row : rows) {

            Association newAssociation = new Association();

            // Set EFO traits
            if (row.getEfoTrait() != null) {
                String[] uris = row.getEfoTrait().split(",");
                Collection<String> efoUris = new ArrayList<>();

                for (String uri : uris) {
                    String trimmedUri = uri.trim();
                    efoUris.add(trimmedUri);
                }

                Collection<EfoTrait> efoTraits = getEfoTraitsFromRepository(efoUris);

                newAssociation.setEfoTraits(efoTraits);
            }


            // Set beta
            newAssociation.setBetaNum(row.getBetaNum());
            newAssociation.setBetaUnit(row.getBetaUnit());
            newAssociation.setBetaDirection(row.getBetaDirection());

            // Set OR
            newAssociation.setOrPerCopyRecip(row.getOrPerCopyRecip());
            newAssociation.setOrPerCopyRecipRange(row.getOrPerCopyRecipRange());
            newAssociation.setOrPerCopyNum(row.getOrPerCopyNum());

            // Set values common to all association types
            newAssociation.setRiskFrequency(row.getAssociationRiskFrequency());
            newAssociation.setPvalueMantissa(row.getPvalueMantissa());
            newAssociation.setPvalueExponent(row.getPvalueExponent());
            newAssociation.setPvalueDescription(row.getPvalueDescription());
            newAssociation.setSnpType(row.getSnpType());
            newAssociation.setStandardError(row.getStandardError());
            newAssociation.setDescription(row.getDescription());
            newAssociation.setRange(row.getRange());

            if (row.getMultiSnpHaplotype().equalsIgnoreCase("Y")) {
                newAssociation.setMultiSnpHaplotype(true);
            }
            else {
                newAssociation.setMultiSnpHaplotype(false);
            }

            if (row.getSnpInteraction().equalsIgnoreCase("Y")) {
                newAssociation.setSnpInteraction(true);
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

                // Add genes to relevant loci, split by 'x' delimiter first
                Collection<Locus> lociWithAddedGenes = new ArrayList<>();

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
                    Collection<Gene> locusGenes = createLocusGenes(interactionGene, ",");
                    locus.setAuthorReportedGenes(locusGenes);
                    geneIndex++;

                    // Set description
                    locus.setDescription("SNP x SNP interaction");

                    // Save our newly created locus
                    locusRepository.save(locus);
                    loci.add(locus);
                }
            }

            // Handle multi-snp and standard snp
            else {
                delimiter = ";";

                // For multi-snp and standard snps we assume their is only one locus
                Locus locus = new Locus();

                // Handle curator entered genes, for haplotype they are separated by a comma
                Collection<Gene> locusGenes = createLocusGenes(row.getAuthorReportedGene(), ",");
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

                // Save our newly created locus
                locusRepository.save(locus);
                loci.add(locus);
            }

            newAssociation.setLoci(loci);

            // Add all newly created associations to collection
            newAssociations.add(newAssociation);
        }
        return newAssociations;
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
        String[] separatedProxies = proxy.split(delimiter);
        for (String separatedProxy : separatedProxies) {
            proxies.add(separatedProxy.trim());
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
                String proxyValue = proxyIterator.next().trim();

                SingleNucleotidePolymorphism newSnp = lociAttributesService.createSnp(snpValue);

                // Create a new risk allele and assign newly created snp
                RiskAllele newRiskAllele = lociAttributesService.createRiskAllele(riskAlleleValue, newSnp);

                // Check for proxies and if we have one create a proxy snp
                Collection<SingleNucleotidePolymorphism> newRiskAlleleProxies = new ArrayList<>();
                if (proxyValue.contains(":")) {
                    String[] splitProxyValues = proxyValue.split(":");

                    for (String splitProxyValue : splitProxyValues) {
                        SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(splitProxyValue.trim());
                        newRiskAlleleProxies.add(proxySnp);
                    }
                }

                else if (proxyValue.contains(",")) {
                    String[] splitProxyValues = proxyValue.split(",");

                    for (String splitProxyValue : splitProxyValues) {
                        SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(splitProxyValue.trim());
                        newRiskAlleleProxies.add(proxySnp);
                    }
                }

                else {
                    SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(proxyValue);
                    newRiskAlleleProxies.add(proxySnp);
                }
                newRiskAllele.setProxySnps(newRiskAlleleProxies);

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

    private Collection<Gene> createLocusGenes(String authorReportedGene, String delimiter) {

        String[] genes = authorReportedGene.split(delimiter);
        Collection<String> genesToCreate = new ArrayList<>();

        for (String gene : genes) {
            String trimmedGene = gene.trim();
            genesToCreate.add(trimmedGene);
        }

        return lociAttributesService.createGene(genesToCreate);
    }

    private Collection<EfoTrait> getEfoTraitsFromRepository(Collection<String> efoUris) {
        Collection<EfoTrait> efoTraits = new ArrayList<>();
        for (String uri : efoUris) {
            String fullUri;
            if (uri.contains("EFO")) {
                fullUri = "http://www.ebi.ac.uk/efo/".concat(uri);
            }
            else if (uri.contains("Orphanet")) {
                fullUri = "http://www.orpha.net/ORDO/".concat(uri);
            }
            else {
                fullUri = "http://purl.obolibrary.org/obo/".concat(uri);
            }

            Collection<EfoTrait> traits = efoTraitRepository.findByUri(fullUri);

            for (EfoTrait trait : traits) {
                efoTraits.add(trait);
            }
        }
        return efoTraits;
    }
}
