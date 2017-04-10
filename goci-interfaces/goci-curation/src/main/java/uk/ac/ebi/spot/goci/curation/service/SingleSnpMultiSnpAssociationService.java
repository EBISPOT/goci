package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationStandardMultiForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormRow;
import uk.ac.ebi.spot.goci.curation.model.SnpMappingForm;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;
import uk.ac.ebi.spot.goci.service.LociAttributesService;

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
public class SingleSnpMultiSnpAssociationService implements SnpAssociationFormService {

    // Repositories
    private GenomicContextRepository genomicContextRepository;

    // Services
    private LociAttributesService lociAttributesService;


    @Autowired
    public SingleSnpMultiSnpAssociationService(GenomicContextRepository genomicContextRepository,
                                               LociAttributesService lociAttributesService) {
        this.genomicContextRepository = genomicContextRepository;
        this.lociAttributesService = lociAttributesService;
    }

    // Creates form which we can then return to view for editing etc.
    @Override public SnpAssociationForm createForm(Association association) {


        SnpAssociationStandardMultiForm form = new SnpAssociationStandardMultiForm();

        // Set association ID
        form.setAssociationId(association.getId());

        // Set simple string and float association attributes
        form.setRiskFrequency(association.getRiskFrequency());
        form.setPvalueDescription(association.getPvalueDescription());
        form.setSnpType(association.getSnpType());
        form.setMultiSnpHaplotype(association.getMultiSnpHaplotype());
        form.setSnpApproved(association.getSnpApproved());
        form.setPvalueMantissa(association.getPvalueMantissa());
        form.setPvalueExponent(association.getPvalueExponent());
        form.setStandardError(association.getStandardError());
        form.setRange(association.getRange());
        form.setDescription(association.getDescription());

        // Set OR/Beta values
        form.setOrPerCopyNum(association.getOrPerCopyNum());
        form.setOrPerCopyRecip(association.getOrPerCopyRecip());
        form.setOrPerCopyRecipRange(association.getOrPerCopyRecipRange());
        form.setBetaNum(association.getBetaNum());
        form.setBetaUnit(association.getBetaUnit());
        form.setBetaDirection(association.getBetaDirection());

        // Add collection of Efo traits
        form.setEfoTraits(association.getEfoTraits());

        // For each locus get genes and risk alleles
        Collection<Locus> loci = association.getLoci();
        Collection<Gene> locusGenes = new ArrayList<>();
        Collection<RiskAllele> locusRiskAlleles = new ArrayList<RiskAllele>();

        // For multi-snp and standard snps we assume their is only one locus
        for (Locus locus : loci) {
            locusGenes.addAll(locus.getAuthorReportedGenes());
            locusRiskAlleles.addAll(locus.getStrongestRiskAlleles());

            // There should only be one locus thus should be safe to set these here
            form.setMultiSnpHaplotypeNum(locus.getHaplotypeSnpCount());
            form.setMultiSnpHaplotypeDescr(locus.getDescription());
        }


        // Get name of gene and add to form
        Collection<String> authorReportedGenes = new ArrayList<>();
        for (Gene locusGene : locusGenes) {
            authorReportedGenes.add(locusGene.getGeneName());
        }
        form.setAuthorReportedGenes(authorReportedGenes);

        // Handle snp rows
        Collection<GenomicContext> snpGenomicContexts = new ArrayList<GenomicContext>();
        Collection<SingleNucleotidePolymorphism> snps = new ArrayList<>();
        List<SnpFormRow> snpFormRows = new ArrayList<SnpFormRow>();
        List<SnpMappingForm> snpMappingForms = new ArrayList<SnpMappingForm>();
        for (RiskAllele riskAllele : locusRiskAlleles) {
            SnpFormRow snpFormRow = new SnpFormRow();
            snpFormRow.setStrongestRiskAllele(riskAllele.getRiskAlleleName());

            SingleNucleotidePolymorphism snp = riskAllele.getSnp();
            snps.add(snp);
            String rsID = snp.getRsId();
            snpFormRow.setSnp(rsID);

            Collection<Location> locations = snp.getLocations();
            for (Location location : locations) {
                SnpMappingForm snpMappingForm = new SnpMappingForm(rsID, location);
                snpMappingForms.add(snpMappingForm);
            }

            // Set proxy if one is present
            Collection<String> proxySnps = new ArrayList<>();
            if (riskAllele.getProxySnps() != null) {
                for (SingleNucleotidePolymorphism riskAlleleProxySnp : riskAllele.getProxySnps()) {
                    proxySnps.add(riskAlleleProxySnp.getRsId());
                }
            }
            snpFormRow.setProxySnps(proxySnps);

            snpGenomicContexts.addAll(genomicContextRepository.findBySnpId(snp.getId()));
            snpFormRows.add(snpFormRow);
        }

        form.setSnpMappingForms(snpMappingForms);
        form.setGenomicContexts(snpGenomicContexts);
        form.setSnps(snps);
        form.setSnpFormRows(snpFormRows);
        return form;
    }

    public Association createAssociation(SnpAssociationStandardMultiForm form) {

        // Set common string, boolean and float association attributes
        Association association = setCommonAssociationElements(form);
        association.setSnpInteraction(false);

        // Add loci to association, for multi-snp and standard snps we assume their is only one locus
        Collection<Locus> loci = new ArrayList<>();
        Locus locus = new Locus();

        // Set locus description and haplotype count
        // Set this number to the number of rows entered by curator
        Integer numberOfRows = form.getSnpFormRows().size();
        if (numberOfRows > 1) {
            locus.setHaplotypeSnpCount(numberOfRows);
            association.setMultiSnpHaplotype(true);
        }

        if (form.getMultiSnpHaplotypeDescr() != null && !form.getMultiSnpHaplotypeDescr().isEmpty()) {
            locus.setDescription(form.getMultiSnpHaplotypeDescr());
        }
        else {
            if (numberOfRows > 1) {
                locus.setDescription(numberOfRows + "-SNP haplotype");
            }
            else {
                locus.setDescription("Single variant");
            }
        }

        // Create gene from each string entered, may sure to check pre-existence
        Collection<String> authorReportedGenes = form.getAuthorReportedGenes();
        Collection<Gene> locusGenes = lociAttributesService.createGene(authorReportedGenes);

        // Set locus genes
        locus.setAuthorReportedGenes(locusGenes);

        // Handle rows entered for haplotype by curator
        Collection<SnpFormRow> rows = form.getSnpFormRows();
        Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();

        for (SnpFormRow row : rows) {

            // Create snps from row information
            String curatorEnteredSNP = row.getSnp();
            SingleNucleotidePolymorphism snp = lociAttributesService.createSnp(curatorEnteredSNP);

            // Get the curator entered risk allele
            String curatorEnteredRiskAllele = row.getStrongestRiskAllele();

            // Create a new risk allele and assign newly created snp
            RiskAllele riskAllele = lociAttributesService.createRiskAllele(curatorEnteredRiskAllele, snp);

            // If association is not a multi-snp haplotype save frequency to risk allele
            if (!form.getMultiSnpHaplotype()) {
                riskAllele.setRiskFrequency(form.getRiskFrequency());
            }

            // Check for proxies and if we have one create a proxy snps
            if (row.getProxySnps() != null && !row.getProxySnps().isEmpty()) {
                Collection<SingleNucleotidePolymorphism> riskAlleleProxySnps = new ArrayList<>();

                for (String curatorEnteredProxySnp : row.getProxySnps()) {
                    SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(curatorEnteredProxySnp);
                    riskAlleleProxySnps.add(proxySnp);
                }

                riskAllele.setProxySnps(riskAlleleProxySnps);
            }

            locusRiskAlleles.add(riskAllele);
        }

        // Assign all created risk alleles to locus
        locus.setStrongestRiskAlleles(locusRiskAlleles);

        // Add locus to collection and link to our association
        loci.add(locus);
        association.setLoci(loci);
        return association;
    }
}