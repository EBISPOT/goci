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
 *         Service class that creates and saves the attributes of a locus
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


    public Collection<Gene> saveGene(Collection<Gene> genes) {
        Collection<Gene> locusGenes = new ArrayList<Gene>();

        genes.forEach(gene -> {
                          // Check if gene already exists
                          Gene geneInDatabase = geneRepository.findByGeneName(gene.getGeneName());

                          // Exists in database already
                          if (geneInDatabase != null) {
                              getLog().debug("Gene " + geneInDatabase.getGeneName() + " already exists in database");
                              locusGenes.add(geneInDatabase);
                          }

                          // If gene doesn't exist then create and save
                          else {
                              // Create new gene
                              getLog().debug("Gene " + gene.getGeneName() + " not found in database. Creating and saving new gene.");
                              geneRepository.save(gene);
                              locusGenes.add(gene);
                          }
                      }

        );
        return locusGenes;
    }

    public Collection<Gene> createGene(Collection<String> authorReportedGenes) {
        Collection<Gene> locusGenes = new ArrayList<Gene>();

        authorReportedGenes.forEach(authorReportedGene -> {
            authorReportedGene = tidy_curator_entered_string(authorReportedGene);

            // Check for intergenic
            if (authorReportedGene.equals("Intergenic")) {
                authorReportedGene = authorReportedGene.toLowerCase();
            }

            Gene newGene = new Gene();
            newGene.setGeneName(authorReportedGene);
            // Add genes to collection
            locusGenes.add(newGene);
        });
        return locusGenes;
    }


    public Collection<RiskAllele> saveRiskAlleles(Collection<RiskAllele> strongestRiskAlleles) {

        //Create new risk allele, at present we always create a new risk allele for each locus within an association

        Collection<RiskAllele> riskAlleles = new ArrayList<RiskAllele>();

        strongestRiskAlleles.forEach(riskAllele -> {

            getLog().info("Saving " + riskAllele.getRiskAlleleName());
            // Save SNP
            SingleNucleotidePolymorphism savedSnp = saveSnp(riskAllele.getSnp());
            riskAllele.setSnp(savedSnp);

            // Save proxy SNPs
            Collection<SingleNucleotidePolymorphism> savedProxySnps = new ArrayList<SingleNucleotidePolymorphism>();
            if (riskAllele.getProxySnps() != null && !riskAllele.getProxySnps().isEmpty()) {
                riskAllele.getProxySnps().forEach(singleNucleotidePolymorphism -> {
                    savedProxySnps.add(saveSnp(singleNucleotidePolymorphism));
                });
            }
            riskAllele.setProxySnps(savedProxySnps);
            riskAlleleRepository.save(riskAllele);
            riskAlleles.add(riskAllele);
        });

        return riskAlleles;
    }


    public RiskAllele createRiskAllele(String curatorEnteredRiskAllele, SingleNucleotidePolymorphism snp) {
        //Create new risk allele, at present we always create a new risk allele for each locus within an association
        RiskAllele riskAllele = new RiskAllele();
        riskAllele.setRiskAlleleName(tidy_curator_entered_string(curatorEnteredRiskAllele));
        riskAllele.setSnp(snp);
        return riskAllele;
    }

    public void deleteRiskAllele(RiskAllele riskAllele) {
        riskAlleleRepository.delete(riskAllele);
    }

    public void deleteLocus(Locus locus) {
        locusRepository.delete(locus);
    }


    private SingleNucleotidePolymorphism saveSnp(SingleNucleotidePolymorphism snp) {

        // Check if SNP already exists
        SingleNucleotidePolymorphism snpInDatabase =
                singleNucleotidePolymorphismRepository.findByRsIdIgnoreCase(snp.getRsId());

        if (snpInDatabase != null) {
            return snpInDatabase;
        }
        else {
            // save new SNP
            return singleNucleotidePolymorphismRepository.save(snp);
        }
    }


    public SingleNucleotidePolymorphism createSnp(String curatorEnteredSNP) {

        // Create new SNP
        SingleNucleotidePolymorphism newSNP = new SingleNucleotidePolymorphism();
        newSNP.setRsId(tidy_curator_entered_string(curatorEnteredSNP));
        return newSNP;
    }


    private String tidy_curator_entered_string(String string) {

        String newString = string.trim();
        String newline = System.getProperty("line.separator");

        if (newString.contains(newline)) {
            newString = newString.replace(newline, "");
        }

        return newString;
    }
}