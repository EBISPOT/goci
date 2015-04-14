package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    // Services
    private AssociationCalculationService associationCalculationService;
    private LociAttributesService lociAttributesService;

    @Autowired
    public SingleSnpMultiSnpAssociationService(AssociationRepository associationRepository,
                                               LocusRepository locusRepository,
                                               AssociationCalculationService associationCalculationService,
                                               LociAttributesService lociAttributesService) {
        this.associationRepository = associationRepository;
        this.locusRepository = locusRepository;
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
        association.setOrPerCopyStdError(snpAssociationForm.getOrPerCopyStdError());
        association.setOrPerCopyUnitDescr(snpAssociationForm.getOrPerCopyUnitDescr());

        // Add collection of EFO traits
        association.setEfoTraits(snpAssociationForm.getEfoTraits());

        // Set mantissa and exponent
        association.setPvalueMantissa(snpAssociationForm.getPvalueMantissa());
        association.setPvalueExponent(snpAssociationForm.getPvalueExponent());

        // Add loci to association or if we are editing an existing one find it
        // For multi-snp and standard snps we assume their is only one locus
        Collection<Locus> loci = new ArrayList<>();
        Locus locus = new Locus();

        // Check for existing locus
        if (snpAssociationForm.getAssociationId() != null) {
            Association associationUserIsEditing = associationRepository.findOne(snpAssociationForm.getAssociationId());
            Collection<Locus> associationLoci = associationUserIsEditing.getLoci();

            // Based on assumption we have only one locus for standard and multi-snp haplotype
            if (associationLoci.size() == 1) {
                for (Locus associationLocus : associationLoci) {
                    locus = associationLocus;
                }
            }

            else {
                throw new RuntimeException(
                        "More than one locus found for association " + association.getId() +
                                ", this is not supported yet"
                );
            }
        }

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
        if (!existingRiskAlleles.isEmpty()){
            locus.setStrongestRiskAlleles(new ArrayList<>());
            for (RiskAllele riskAllele:existingRiskAlleles){
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
        snpAssociationForm.setOrPerCopyUnitDescr(association.getOrPerCopyUnitDescr());

        // Calculate p-value float, this will appear in table for curators
        Integer pvalueMantissa = snpAssociationForm.getPvalueMantissa();
        Integer pvalueExponent = snpAssociationForm.getPvalueExponent();

        if (pvalueMantissa != null && pvalueExponent != null) {
            snpAssociationForm.setPvalueFloat(associationCalculationService.calculatePvalueFloat(pvalueMantissa,
                                                                                                 pvalueExponent));
        }
        else {
            snpAssociationForm.setPvalueFloat(Float.valueOf(0));
        }

        // Add collection of Efo traits
        snpAssociationForm.setEfoTraits(association.getEfoTraits());

        // For each locus get genes and risk alleles
        Collection<Locus> loci = association.getLoci();
        Collection<Gene> locusGenes = new ArrayList<>();
        Collection<RiskAllele> locusRiskAlleles = new ArrayList<RiskAllele>();

        // For multi-snp and standard snps we assume their is only one locus
        if (loci.size() == 1) {
            for (Locus locus : loci) {
                locusGenes.addAll(locus.getAuthorReportedGenes());
                locusRiskAlleles.addAll(locus.getStrongestRiskAlleles());

                // There should only be one locus thus should be safe to set these here
                snpAssociationForm.setMultiSnpHaplotypeNum(locus.getHaplotypeSnpCount());
                snpAssociationForm.setMultiSnpHaplotypeDescr(locus.getDescription());
            }
        }

        else {
            throw new RuntimeException(
                    "More than one locus found for association " + association.getId() + ", this is not supported yet"
            );
        }

        // Get name of gene and add to form
        Collection<String> authorReportedGenes = new ArrayList<>();
        for (Gene locusGene : locusGenes) {
            authorReportedGenes.add(locusGene.getGeneName());
        }
        snpAssociationForm.setAuthorReportedGenes(authorReportedGenes);

        // Handle snp rows and return region details for each snp
        // Note region is never edited by curator so only appears in table but never in
        // any edit forms
        Collection<Region> snpRegions = new ArrayList<Region>();
        List<SnpFormRow> snpFormRows = new ArrayList<SnpFormRow>();
        for (RiskAllele riskAllele : locusRiskAlleles) {
            SnpFormRow snpFormRow = new SnpFormRow();
            snpFormRow.setStrongestRiskAllele(riskAllele.getRiskAlleleName());
            snpFormRow.setSnp(riskAllele.getSnp().getRsId());
            snpRegions.addAll(riskAllele.getSnp().getRegions());
            snpFormRows.add(snpFormRow);
        }

        snpAssociationForm.setRegions(snpRegions);
        snpAssociationForm.setSnpFormRows(snpFormRows);
        return snpAssociationForm;
    }


}
