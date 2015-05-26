package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.GeneRepository;
import uk.ac.ebi.spot.goci.repository.LocusRepository;
import uk.ac.ebi.spot.goci.repository.RiskAlleleRepository;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by emma on 13/04/2015.
 *
 * @author emma
 *         <p>
 *         Service class that creates the attributes of a loci, used by AssociationController
 */
@Service
public class LociAttributesService {

    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private GeneRepository geneRepository;
    private RiskAlleleRepository riskAlleleRepository;
    private LocusRepository locusRepository;

    // Constructor
    @Autowired
    public LociAttributesService(SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository,
                                 GeneRepository geneRepository,
                                 RiskAlleleRepository riskAlleleRepository,
                                 LocusRepository locusRepository) {
        this.singleNucleotidePolymorphismRepository = singleNucleotidePolymorphismRepository;
        this.geneRepository = geneRepository;
        this.riskAlleleRepository = riskAlleleRepository;
        this.locusRepository = locusRepository;
    }

    public Collection<Gene> createGene(Collection<String> authorReportedGenes) {
        Collection<Gene> locusGenes = new ArrayList<Gene>();
        for (String authorReportedGene : authorReportedGenes) {

            // Check if gene already exists, note we may have duplicates so for moment just take first one
            List<Gene> genesInDatabase = geneRepository.findByGeneNameIgnoreCase(authorReportedGene);
            Gene gene;

            // Exists in database already
            if (genesInDatabase.size() > 0) {
                gene = genesInDatabase.get(0);
            }

            // If gene doesn't exist then create and save
            else {
                // Create new gene
                Gene newGene = new Gene();
                newGene.setGeneName(authorReportedGene);

                // Save gene
                gene = geneRepository.save(newGene);
            }

            // Add genes to collection
            locusGenes.add(gene);
        }
        return locusGenes;
    }

    public RiskAllele createRiskAllele(String curatorEnteredRiskAllele, SingleNucleotidePolymorphism snp) {

        //Create new risk allele, at present we always create a new risk allele for each locus within an association
        RiskAllele riskAllele = new RiskAllele();
        riskAllele.setRiskAlleleName(curatorEnteredRiskAllele);
        riskAllele.setSnp(snp);

        // Save risk allele
        riskAlleleRepository.save(riskAllele);
        return riskAllele;
    }

    public void deleteRiskAllele(RiskAllele riskAllele) {
        riskAlleleRepository.delete(riskAllele);
    }

    public void deleteLocus (Locus locus){
        locusRepository.delete(locus);
    }
    

    public SingleNucleotidePolymorphism createSnp(String curatorEnteredSNP) {

        // Check if SNP already exists database, note database contains duplicates
        List<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms =
                singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(curatorEnteredSNP);
        SingleNucleotidePolymorphism snp;
        if (singleNucleotidePolymorphisms.size() > 0) {
            snp = singleNucleotidePolymorphisms.get(0);
        }

        // If SNP doesn't exist, create and save
        else {
            // Create new SNP
            SingleNucleotidePolymorphism newSNP = new SingleNucleotidePolymorphism();
            newSNP.setRsId(curatorEnteredSNP);

            // Save SNP
            snp = singleNucleotidePolymorphismRepository.save(newSNP);
        }

        return snp;

    }
}
