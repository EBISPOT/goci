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

    private LociAttributesService lociAttributesService;

    @Autowired
    public SnpInteractionAssociationService(LociAttributesService lociAttributesService) {
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

        // Set multi-snp and snp interaction checkboxes
        association.setMultiSnpHaplotype(false);
        association.setSnpInteraction(true);

        // Add collection of EFO traits
        association.setEfoTraits(snpAssociationInteractionForm.getEfoTraits());

        // Set mantissa and exponent
        association.setPvalueMantissa(snpAssociationInteractionForm.getPvalueMantissa());
        association.setPvalueExponent(snpAssociationInteractionForm.getPvalueExponent());

        // For each column create a loci
        Collection<Locus> loci = new ArrayList<>();
        for (SnpFormColumn col : snpAssociationInteractionForm.getSnpFormColumns()) {

            Locus locus = new Locus();
            locus.setDescription("SNP x SNP interaction");

            // Create SNP
            String curatorEnteredSNP = col.getSnp();
            SingleNucleotidePolymorphism snp = lociAttributesService.createSnp(curatorEnteredSNP);

            // One risk allele per locus
            String curatorEnteredRiskAllele = col.getStrongestRiskAllele();
            RiskAllele riskAllele = lociAttributesService.createRiskAllele(curatorEnteredRiskAllele, snp);
            Collection<RiskAllele> locusRiskAlleles = new ArrayList<>();

            // Check for a proxy and if we have one create a proxy snp
            Collection<String> curatorEnteredProxySnps = col.getProxies();
            if (curatorEnteredProxySnps != null && !curatorEnteredProxySnps.isEmpty()) {
                for (String proxy : curatorEnteredProxySnps) {
                    SingleNucleotidePolymorphism proxySnp = lociAttributesService.createSnp(proxy);

                    // Add proxy to collection of proxy snps and link to risk allele
                    // ...

                }
                // TODO NEED TO UPDATE RISK ALLELE MODEL SO IT CAN HOLD MORE THAN ON PROXY

            }

            // Link risk allele to locus
            locus.setStrongestRiskAlleles(locusRiskAlleles);

            // Create genes
            Collection<String> authorReportedGenes = col.getAuthorReportedGenes();
            Collection<Gene> locusGenes = lociAttributesService.createGene(authorReportedGenes);

            // Set locus genes
            locus.setAuthorReportedGenes(locusGenes);

            // TODO WHAT DO WE DO WITH RISK FREQUENCY

        }

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
                Collection<String> proxies = new ArrayList<>();

                // For snp x snp interaction studies should only have one risk allele per locus
                if (locusRiskAlleles != null && locusRiskAlleles.size() == 1) {
                    for (RiskAllele riskAllele : locusRiskAlleles) {
                        strongestRiskAllele = riskAllele.getRiskAlleleName();
                        snp = riskAllele.getSnp().getRsId();

                        // Set proxy
                        proxies.add(riskAllele.getProxySnp().getRsId());
                    }
                }

                else {
                    throw new RuntimeException(
                            "More than one risk allele found for locus " + locus.getId() +
                                    ", this is not supported yet for SNP interaction associations"
                    );
                }
                snpFormColumn.setStrongestRiskAllele(strongestRiskAllele);
                snpFormColumn.setSnp(snp);
                snpFormColumn.setProxies(proxies);
                snpFormColumns.add(snpFormColumn);

                //TODO RISK FREQUENCY, GENOMEWIDE , LIMITED LIST
            }
        }

        snpAssociationInteractionForm.setSnpFormColumns(snpFormColumns);
        return snpAssociationInteractionForm;
    }


}
