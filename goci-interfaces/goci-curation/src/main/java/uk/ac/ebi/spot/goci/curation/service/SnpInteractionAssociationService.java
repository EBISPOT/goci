package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.SnpAssociationInteractionForm;
import uk.ac.ebi.spot.goci.curation.model.SnpFormColumn;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;

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
public class SnpInteractionAssociationService {

    // Repositories
    private LocusRepository locusRepository;
    private AssociationRepository associationRepository;

    // Services
    private LociAttributesService lociAttributesService;

    @Autowired
    public SnpInteractionAssociationService(LocusRepository locusRepository,
                                            AssociationRepository associationRepository,
                                            LociAttributesService lociAttributesService) {
        this.locusRepository = locusRepository;
        this.associationRepository = associationRepository;
        this.lociAttributesService = lociAttributesService;
    }

    public Association createAssociation(SnpAssociationInteractionForm snpAssociationInteractionForm) {

        Association association = new Association();

        // Set simple string, boolean and float association attributes
        association.setPvalueText(snpAssociationInteractionForm.getPvalueText());
        association.setOrType(snpAssociationInteractionForm.getOrType());
        association.setSnpType(snpAssociationInteractionForm.getSnpType());
        association.setSnpChecked(snpAssociationInteractionForm.getSnpChecked());
        association.setOrPerCopyNum(snpAssociationInteractionForm.getOrPerCopyNum());
        association.setOrPerCopyRecip(snpAssociationInteractionForm.getOrPerCopyRecip());
        association.setOrPerCopyRange(snpAssociationInteractionForm.getOrPerCopyRange());
        association.setOrPerCopyRecipRange(snpAssociationInteractionForm.getOrPerCopyRecipRange());
        association.setOrPerCopyStdError(snpAssociationInteractionForm.getOrPerCopyStdError());
        association.setOrPerCopyUnitDescr(snpAssociationInteractionForm.getOrPerCopyUnitDescr());
        association.setRiskFrequency(snpAssociationInteractionForm.getRiskFrequency());

        // Set multi-snp and snp interaction checkboxes
        association.setMultiSnpHaplotype(false);
        association.setSnpInteraction(true);

        // Add collection of EFO traits
        association.setEfoTraits(snpAssociationInteractionForm.getEfoTraits());

        // Set mantissa and exponent
        association.setPvalueMantissa(snpAssociationInteractionForm.getPvalueMantissa());
        association.setPvalueExponent(snpAssociationInteractionForm.getPvalueExponent());

        // Check for existing loci, when editing delete any existing loci and risk alleles
        // They will be recreated in next for loop
        if (snpAssociationInteractionForm.getAssociationId() != null) {

            Association associationUserIsEditing =
                    associationRepository.findOne(snpAssociationInteractionForm.getAssociationId());
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

        // For each column create a loci
        Collection<Locus> loci = new ArrayList<>();
        for (SnpFormColumn col : snpAssociationInteractionForm.getSnpFormColumns()) {

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

            // Save our newly created locus
            locusRepository.save(locus);

            // Add locus to collection and link to our association
            loci.add(locus);

        }
        association.setLoci(loci);
        return association;
    }

    // Create a form to return to view from Association model object
    public SnpAssociationInteractionForm createSnpAssociationInteractionForm(Association association) {

        // Create form
        SnpAssociationInteractionForm snpAssociationInteractionForm = new SnpAssociationInteractionForm();

        // Set simple string and boolean values
        snpAssociationInteractionForm.setAssociationId(association.getId());
        snpAssociationInteractionForm.setPvalueText(association.getPvalueText());
        snpAssociationInteractionForm.setOrPerCopyNum(association.getOrPerCopyNum());
        snpAssociationInteractionForm.setSnpType(association.getSnpType());
        snpAssociationInteractionForm.setSnpChecked(association.getSnpChecked());
        snpAssociationInteractionForm.setOrType(association.getOrType());
        snpAssociationInteractionForm.setPvalueMantissa(association.getPvalueMantissa());
        snpAssociationInteractionForm.setPvalueExponent(association.getPvalueExponent());
        snpAssociationInteractionForm.setOrPerCopyRecip(association.getOrPerCopyRecip());
        snpAssociationInteractionForm.setOrPerCopyStdError(association.getOrPerCopyStdError());
        snpAssociationInteractionForm.setOrPerCopyRange(association.getOrPerCopyRange());
        snpAssociationInteractionForm.setOrPerCopyRecipRange(association.getOrPerCopyRecipRange());
        snpAssociationInteractionForm.setOrPerCopyUnitDescr(association.getOrPerCopyUnitDescr());
        snpAssociationInteractionForm.setRiskFrequency(association.getRiskFrequency());

        // Add collection of Efo traits
        snpAssociationInteractionForm.setEfoTraits(association.getEfoTraits());

        // Create form columns
        List<SnpFormColumn> snpFormColumns = new ArrayList<>();

        // For each locus get genes and risk alleles
        Collection<Locus> loci = association.getLoci();

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

        snpAssociationInteractionForm.setSnpFormColumns(snpFormColumns);
        snpAssociationInteractionForm.setNumOfInteractions(snpFormColumns.size());
        return snpAssociationInteractionForm;
    }


}
