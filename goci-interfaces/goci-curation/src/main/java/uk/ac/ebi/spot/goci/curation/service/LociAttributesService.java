package uk.ac.ebi.spot.goci.curation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

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

        authorReportedGenes.forEach(authorReportedGene -> {
            authorReportedGene = tidy_curator_entered_string(authorReportedGene);

            // Check for intergenic
            if (authorReportedGene.equals("Intergenic")) {
                authorReportedGene = authorReportedGene.toLowerCase();
            }

            // Check if gene already exists, note we may have duplicates so for moment just take first one
            Gene geneInDatabase = geneRepository.findByGeneName(authorReportedGene);
            Gene gene;

            // Exists in database already
            if (geneInDatabase != null) {
                getLog().debug("Gene " + geneInDatabase.getGeneName() + " already exists in database");
                gene = geneInDatabase;
            }

            // If gene doesn't exist then create and save
            else {
                // Create new gene
                getLog().debug("Gene " + authorReportedGene + " not found in database. Creating and saving new gene.");
                Gene newGene = new Gene();
                newGene.setGeneName(authorReportedGene);

                // Save gene
                gene = geneRepository.save(newGene);
            }

            // Add genes to collection
            locusGenes.add(gene);
        });
        return locusGenes;
    }

    public RiskAllele createRiskAllele(String curatorEnteredRiskAllele, SingleNucleotidePolymorphism snp) {

        //Create new risk allele, at present we always create a new risk allele for each locus within an association
        RiskAllele riskAllele = new RiskAllele();
        riskAllele.setRiskAlleleName(tidy_curator_entered_string(curatorEnteredRiskAllele));
        riskAllele.setSnp(snp);

        // Save risk allele
        riskAlleleRepository.save(riskAllele);
        return riskAllele;
    }

    public void deleteRiskAllele(RiskAllele riskAllele) {
        riskAlleleRepository.delete(riskAllele);
    }

    public void deleteLocus(Locus locus) {
        locusRepository.delete(locus);
    }

    public SingleNucleotidePolymorphism createSnp(String curatorEnteredSNP) {

        curatorEnteredSNP = tidy_curator_entered_string(curatorEnteredSNP);

        // Check if SNP already exists database
        SingleNucleotidePolymorphism snpInDatabase =
                singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(curatorEnteredSNP);
        SingleNucleotidePolymorphism snp;
        if (snpInDatabase != null) {
            snp = snpInDatabase;
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


    private String tidy_curator_entered_string(String string) {

        String newString = string.trim();
        String newline = System.getProperty("line.separator");

        if (newString.contains(newline)) {
            newString = newString.replace(newline, "");
        }

        // catch common typo in standard RS_IDs
        if (newString.startsWith("Rs")) {
            newString = newString.toLowerCase();
        }

        return newString;
    }
}
