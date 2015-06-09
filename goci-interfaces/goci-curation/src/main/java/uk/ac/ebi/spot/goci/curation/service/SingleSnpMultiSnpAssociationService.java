package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.curation.model.SnpMappingForm;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.AssociationReport;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by emma on 13/04/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates an association or returns a view of an association, used by AssociationController.
 *         Assumes we only have one locus for associations of type: single and multi-snp haplotypes
 */
@Service
public class SingleSnpMultiSnpAssociationService {

    // Repositories
    private AssociationRepository associationRepository;
    private LocusRepository locusRepository;
    private GenomicContextRepository genomicContextRepository;

    // Services
    private AssociationCalculationService associationCalculationService;
    private LociAttributesService lociAttributesService;

    @Autowired
    public SingleSnpMultiSnpAssociationService(AssociationRepository associationRepository,
                                               LocusRepository locusRepository,
                                               GenomicContextRepository genomicContextRepository,
                                               AssociationCalculationService associationCalculationService,
                                               LociAttributesService lociAttributesService
                                              ) {
        this.associationRepository = associationRepository;
        this.locusRepository = locusRepository;
        this.genomicContextRepository = genomicContextRepository;
        this.associationCalculationService = associationCalculationService;
        this.lociAttributesService = lociAttributesService;

    }

    public Association createAssociation(SnpAssociationForm snpAssociationForm) {

        Association association = new Association();

        // Set simple string, boolean and float association attributes
        association.setRiskFrequency(snpAssociationForm.getRiskFrequency());
        association.setPvalueText(snpAssociationForm.getPvalueText());
        association.setOrType(snpAssociationForm.getOrType());
        association.setSnpType(snpAssociationForm.getSnpType());
        association.setMultiSnpHaplotype(snpAssociationForm.getMultiSnpHaplotype());
        association.setSnpInteraction(snpAssociationForm.getSnpInteraction());
        association.setSnpChecked(snpAssociationForm.getSnpChecked());
        association.setOrPerCopyNum(snpAssociationForm.getOrPerCopyNum());
        association.setOrPerCopyRecip(snpAssociationForm.getOrPerCopyRecip());
        association.setOrPerCopyRange(snpAssociationForm.getOrPerCopyRange());
        association.setOrPerCopyRecipRange(snpAssociationForm.getOrPerCopyRecipRange());
        association.setOrPerCopyStdError(snpAssociationForm.getOrPerCopyStdError());
        association.setOrPerCopyUnitDescr(snpAssociationForm.getOrPerCopyUnitDescr());

        // Set value by default to false
        association.setSnpInteraction(false);

        // Add collection of EFO traits
        association.setEfoTraits(snpAssociationForm.getEfoTraits());

        // Set mantissa and exponent
        association.setPvalueMantissa(snpAssociationForm.getPvalueMantissa());
        association.setPvalueExponent(snpAssociationForm.getPvalueExponent());

        // Check for existing loci, when editing delete any existing loci and risk alleles
        // They will be recreated in next for loop
        if (snpAssociationForm.getAssociationId() != null) {
            Association associationUserIsEditing = associationRepository.findOne(snpAssociationForm.getAssociationId());
            Collection<Locus> associationLoci = associationUserIsEditing.getLoci();
            Collection<RiskAllele> existingRiskAlleles = new ArrayList<>();

            if (associationLoci != null) {
                for (Locus locus : associationLoci) {
                    existingRiskAlleles.addAll(locus.getStrongestRiskAlleles());
                }
                for (Locus locus : associationLoci) {
                    lociAttributesService.deleteLocus(locus);
                }
                for (RiskAllele existingRiskAllele : existingRiskAlleles) {
                    lociAttributesService.deleteRiskAllele(existingRiskAllele);
                }
            }

        }

        // Add loci to association or if we are editing an existing one find it
        // For multi-snp and standard snps we assume their is only one locus
        Collection<Locus> loci = new ArrayList<>();
        Locus locus = new Locus();

        // Set locus description and haplotype count
        // Set this number to the number of rows entered by curator
        Integer numberOfRows = snpAssociationForm.getSnpFormRows().size();
        if (numberOfRows > 1) {
            locus.setHaplotypeSnpCount(numberOfRows);
        }

        locus.setDescription(snpAssociationForm.getMultiSnpHaplotypeDescr());

        // Create gene from each string entered, may sure to check pre-existence
        Collection<String> authorReportedGenes = snpAssociationForm.getAuthorReportedGenes();
        Collection<Gene> locusGenes = lociAttributesService.createGene(authorReportedGenes);

        // Set locus genes
        locus.setAuthorReportedGenes(locusGenes);

        // Delete any existing risk alleles as we will re-create in next for loop
        // This should only occur if we are editing an existing study
        Collection<RiskAllele> existingRiskAlleles = locus.getStrongestRiskAlleles();
        if (!existingRiskAlleles.isEmpty()) {
            locus.setStrongestRiskAlleles(new ArrayList<>());
            for (RiskAllele riskAllele : existingRiskAlleles) {
                lociAttributesService.deleteRiskAllele(riskAllele);
            }
        }

        // Handle rows entered for haplotype by curator
        Collection<SnpFormRow> rows = snpAssociationForm.getSnpFormRows();
        Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();

        for (SnpFormRow row : rows) {

            // Create snps from row information
            String curatorEnteredSNP = row.getSnp();
            SingleNucleotidePolymorphism snp = lociAttributesService.createSnp(curatorEnteredSNP);

            // Get the curator entered risk allele
            String curatorEnteredRiskAllele = row.getStrongestRiskAllele();

            // Create a new risk allele and assign newly created snp
            RiskAllele riskAllele = lociAttributesService.createRiskAllele(curatorEnteredRiskAllele, snp);

            // Check for a proxy and if we have one create a proxy snp
            if (row.getProxySnp() != null && !row.getProxySnp().isEmpty()) {
                String curatorEnteredProxySnp = row.getProxySnp();
                SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(curatorEnteredProxySnp);
                riskAllele.setProxySnp(proxySnp);
            }

            locusRiskAlleles.add(riskAllele);
        }

        // Assign all created risk alleles to locus
        locus.setStrongestRiskAlleles(locusRiskAlleles);

        // Save our newly created locus
        locusRepository.save(locus);

        // Add locus to collection and link to our association
        loci.add(locus);
        association.setLoci(loci);

        return association;

    }


    // Creates form which we can then return to view for editing etc.
    public SnpAssociationForm createSnpAssociationForm(Association association) {

        SnpAssociationForm snpAssociationForm = new SnpAssociationForm();

        // Set association ID
        snpAssociationForm.setAssociationId(association.getId());

        // Set simple string and float association attributes
        snpAssociationForm.setRiskFrequency(association.getRiskFrequency());
        snpAssociationForm.setPvalueText(association.getPvalueText());
        snpAssociationForm.setOrPerCopyNum(association.getOrPerCopyNum());
        snpAssociationForm.setOrType(association.getOrType());
        snpAssociationForm.setSnpType(association.getSnpType());
        snpAssociationForm.setMultiSnpHaplotype(association.getMultiSnpHaplotype());
        snpAssociationForm.setSnpChecked(association.getSnpChecked());
        snpAssociationForm.setSnpInteraction(association.getSnpInteraction());
        snpAssociationForm.setPvalueMantissa(association.getPvalueMantissa());
        snpAssociationForm.setPvalueExponent(association.getPvalueExponent());
        snpAssociationForm.setOrPerCopyRecip(association.getOrPerCopyRecip());
        snpAssociationForm.setOrPerCopyStdError(association.getOrPerCopyStdError());
        snpAssociationForm.setOrPerCopyRange(association.getOrPerCopyRange());
        snpAssociationForm.setOrPerCopyRecipRange(association.getOrPerCopyRecipRange());
        snpAssociationForm.setOrPerCopyUnitDescr(association.getOrPerCopyUnitDescr());


        // Add collection of Efo traits
        snpAssociationForm.setEfoTraits(association.getEfoTraits());

        // For each locus get genes and risk alleles
        Collection<Locus> loci = association.getLoci();
        Collection<Gene> locusGenes = new ArrayList<>();
        Collection<RiskAllele> locusRiskAlleles = new ArrayList<RiskAllele>();

        // For multi-snp and standard snps we assume their is only one locus
        for (Locus locus : loci) {
            locusGenes.addAll(locus.getAuthorReportedGenes());
            locusRiskAlleles.addAll(locus.getStrongestRiskAlleles());

            // There should only be one locus thus should be safe to set these here
            snpAssociationForm.setMultiSnpHaplotypeNum(locus.getHaplotypeSnpCount());
            snpAssociationForm.setMultiSnpHaplotypeDescr(locus.getDescription());
        }


        Collection<String> authorReportedGenes = new ArrayList<>();
        for (Gene locusGene : locusGenes) {
            authorReportedGenes.add(locusGene.getGeneName());
        }
        snpAssociationForm.setAuthorReportedGenes(authorReportedGenes);

        // Handle snp rows
        Collection<Location> snpLocations = new ArrayList<Location>();
        Collection<GenomicContext> snpGenomicContexts = new ArrayList<GenomicContext>();
        List<SnpFormRow> snpFormRows = new ArrayList<SnpFormRow>();
        List<SnpMappingForm> snpMappingForms = new ArrayList<SnpMappingForm>();
        for (RiskAllele riskAllele : locusRiskAlleles) {
            SnpFormRow snpFormRow = new SnpFormRow();
            snpFormRow.setStrongestRiskAllele(riskAllele.getRiskAlleleName());

            SingleNucleotidePolymorphism snp = riskAllele.getSnp();
            String rsID = snp.getRsId();
            snpFormRow.setSnp(riskAllele.getSnp().getRsId());

            Collection<Location> locations = snp.getLocations();
            for (Location location : locations) {
                SnpMappingForm snpMappingForm = new SnpMappingForm(rsID,location);
                snpMappingForms.add(snpMappingForm);
            }

            // Set proxy if one is present
            if (riskAllele.getProxySnp() != null) {
                snpFormRow.setProxySnp(riskAllele.getProxySnp().getRsId());
            }
            else { snpFormRow.setProxySnp(null);}

            snpGenomicContexts.addAll(genomicContextRepository.findBySnpId(snp.getId()));
            snpFormRows.add(snpFormRow);
        }

        snpAssociationForm.setSnpMappingForms(snpMappingForms);
        snpAssociationForm.setGenomicContexts(snpGenomicContexts);
        snpAssociationForm.setSnpFormRows(snpFormRows);
        return snpAssociationForm;
    }
}
