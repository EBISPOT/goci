package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationForm;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
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
 * Created by emma on 20/05/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates an association or returns a view of an association, used by AssociationController.
 *         Used only for SNP X SNP interaction associations
 */
@Service
public class SnpInteractionAssociationService implements SnpAssociationFormService {

    // Repositories
    private GenomicContextRepository genomicContextRepository;

    // Services
    private LociAttributesService lociAttributesService;

    @Autowired
    public SnpInteractionAssociationService(GenomicContextRepository genomicContextRepository,
                                            LociAttributesService lociAttributesService) {
        this.genomicContextRepository = genomicContextRepository;
        this.lociAttributesService = lociAttributesService;
    }

    // Create a form to return to view from Association model object
    @Override public SnpAssociationForm createForm(Association association) {

        // Create form
        SnpAssociationInteractionForm form = new SnpAssociationInteractionForm();

        // Set simple string and boolean values
        form.setAssociationId(association.getId());
        form.setPvalueDescription(association.getPvalueDescription());
        form.setSnpType(association.getSnpType());
        form.setSnpApproved(association.getSnpApproved());
        form.setPvalueMantissa(association.getPvalueMantissa());
        form.setPvalueExponent(association.getPvalueExponent());
        form.setStandardError(association.getStandardError());
        form.setRange(association.getRange());
        form.setDescription(association.getDescription());
        form.setRiskFrequency(association.getRiskFrequency());

        // Set OR/Beta values
        form.setOrPerCopyNum(association.getOrPerCopyNum());
        form.setOrPerCopyRecip(association.getOrPerCopyRecip());
        form.setOrPerCopyRecipRange(association.getOrPerCopyRecipRange());
        form.setBetaNum(association.getBetaNum());
        form.setBetaUnit(association.getBetaUnit());
        form.setBetaDirection(association.getBetaDirection());

        // Add collection of Efo traits
        form.setEfoTraits(association.getEfoTraits());

        // Create form columns
        List<SnpFormColumn> snpFormColumns = new ArrayList<>();

        // For each locus get genes and risk alleles
        Collection<Locus> loci = association.getLoci();

        Collection<GenomicContext> snpGenomicContexts = new ArrayList<GenomicContext>();
        Collection<SingleNucleotidePolymorphism> snps = new ArrayList<SingleNucleotidePolymorphism>();
        List<SnpMappingForm> snpMappingForms = new ArrayList<SnpMappingForm>();

        // Create a column per locus
        if (loci != null && !loci.isEmpty()) {
            for (Locus locus : loci) {

                SnpFormColumn snpFormColumn = new SnpFormColumn();

                // Set genes
                Collection<String> authorReportedGenes = new ArrayList<>();
                for (Gene gene : locus.getAuthorReportedGenes()) {
                    authorReportedGenes.add(gene.getGeneName());
                }
                snpFormColumn.setAuthorReportedGenes(authorReportedGenes);

                // Set risk allele
                Collection<RiskAllele> locusRiskAlleles = locus.getStrongestRiskAlleles();
                String strongestRiskAllele = null;
                String snp = null;
                Collection<String> proxySnps = new ArrayList<>();
                Boolean genomeWide = false;
                Boolean limitedList = false;
                String riskFrequency = null;

                // For snp x snp interaction studies should only have one risk allele per locus
                if (locusRiskAlleles != null && locusRiskAlleles.size() == 1) {
                    for (RiskAllele riskAllele : locusRiskAlleles) {
                        strongestRiskAllele = riskAllele.getRiskAlleleName();
                        snp = riskAllele.getSnp().getRsId();

                        SingleNucleotidePolymorphism snp_obj = riskAllele.getSnp();
                        snps.add(snp_obj);
                        Collection<Location> locations = snp_obj.getLocations();
                        for (Location location : locations) {
                            SnpMappingForm snpMappingForm = new SnpMappingForm(snp, location);
                            snpMappingForms.add(snpMappingForm);
                        }
                        snpGenomicContexts.addAll(genomicContextRepository.findBySnpId(snp_obj.getId()));

                        // Set proxy
                        if (riskAllele.getProxySnps() != null) {
                            for (SingleNucleotidePolymorphism riskAlleleProxySnp : riskAllele.getProxySnps()) {
                                proxySnps.add(riskAlleleProxySnp.getRsId());
                            }
                        }

                        if (riskAllele.getGenomeWide() != null && riskAllele.getGenomeWide()) {
                            genomeWide = true;
                        }

                        if (riskAllele.getLimitedList() != null && riskAllele.getLimitedList()) {
                            limitedList = true;
                        }

                        riskFrequency = riskAllele.getRiskFrequency();
                    }
                }

                else {
                    throw new RuntimeException(
                            "More than one risk allele found for locus " + locus.getId() +
                                    ", this is not supported yet for SNP interaction associations"
                    );
                }

                // Set column attributes
                snpFormColumn.setStrongestRiskAllele(strongestRiskAllele);
                snpFormColumn.setSnp(snp);
                snpFormColumn.setProxySnps(proxySnps);
                snpFormColumn.setGenomeWide(genomeWide);
                snpFormColumn.setLimitedList(limitedList);
                snpFormColumn.setRiskFrequency(riskFrequency);


                snpFormColumns.add(snpFormColumn);
            }
        }
        form.setSnpMappingForms(snpMappingForms);
        form.setGenomicContexts(snpGenomicContexts);
        form.setSnps(snps);
        form.setSnpFormColumns(snpFormColumns);
        form.setNumOfInteractions(snpFormColumns.size());
        return form;
    }

    public Association createAssociation(SnpAssociationInteractionForm form) {

        // Set simple string, boolean and float association attributes
        Association association = setCommonAssociationElements(form);

        // Set multi-snp and snp interaction checkboxes
        association.setMultiSnpHaplotype(false);
        association.setSnpInteraction(true);

        // For each column create a loci
        Collection<Locus> loci = new ArrayList<>();
        for (SnpFormColumn col : form.getSnpFormColumns()) {

            Locus locus = new Locus();
            locus.setDescription("SNP x SNP interaction");

            // Set locus genes
            Collection<String> authorReportedGenes = col.getAuthorReportedGenes();
            Collection<Gene> locusGenes = lociAttributesService.createGene(authorReportedGenes);
            locus.setAuthorReportedGenes(locusGenes);

            // Create SNP
            String curatorEnteredSNP = col.getSnp();
            SingleNucleotidePolymorphism snp = lociAttributesService.createSnp(curatorEnteredSNP);

            // One risk allele per locus
            String curatorEnteredRiskAllele = col.getStrongestRiskAllele();
            RiskAllele riskAllele = lociAttributesService.createRiskAllele(curatorEnteredRiskAllele, snp);
            Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();

            // Set risk allele attributes
            riskAllele.setGenomeWide(col.getGenomeWide());
            riskAllele.setLimitedList(col.getLimitedList());
            riskAllele.setRiskFrequency(col.getRiskFrequency());

            // Check for a proxy and if we have one create a proxy snp
            Collection<String> curatorEnteredProxySnps = col.getProxySnps();
            if (curatorEnteredProxySnps != null && !curatorEnteredProxySnps.isEmpty()) {

                Collection<SingleNucleotidePolymorphism> riskAlleleProxySnps = new ArrayList<>();

                for (String curatorEnteredProxySnp : curatorEnteredProxySnps) {
                    SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(curatorEnteredProxySnp);
                    riskAlleleProxySnps.add(proxySnp);
                }

                riskAllele.setProxySnps(riskAlleleProxySnps);
            }

            // Link risk allele to locus
            locusRiskAlleles.add(riskAllele);
            locus.setStrongestRiskAlleles(locusRiskAlleles);

            // Add locus to collection and link to our association
            loci.add(locus);

        }
        association.setLoci(loci);
        return association;
    }
}