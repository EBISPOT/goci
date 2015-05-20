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

/**
 * Created by emma on 20/05/2015.
 * @author emma
 *
 *  Service class that creates an association or returns a view of an association, used by AssociationController.
 *  Used only for SNP X SNP interaction associations
 */
@Service
public class SnpInteractionAssociationService {

    private LociAttributesService lociAttributesService;

    @Autowired
    public SnpInteractionAssociationService(LociAttributesService lociAttributesService) {
        this.lociAttributesService = lociAttributesService;
    }

    public Association createAssociation(SnpAssociationInteractionForm snpAssociationInteractionForm){

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
        for(SnpFormColumn col: snpAssociationInteractionForm.getSnpFormColumns()){

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
            Collection<String>curatorEnteredProxySnps = col.getProxies();
            if (curatorEnteredProxySnps != null && !curatorEnteredProxySnps.isEmpty()) {
                for (String proxy : curatorEnteredProxySnps){
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

}
