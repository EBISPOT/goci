package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.utils.StringProcessingService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emma on 12/04/2016.
 * @author emma
 *
 * Creates common association attributes
 */
@Service
public class AssociationAttributeService {

    private EfoTraitRepository efoTraitRepository;

    @Autowired
    public AssociationAttributeService(EfoTraitRepository efoTraitRepository) {
        this.efoTraitRepository = efoTraitRepository;
    }

    public Collection<Gene> createLocusGenes(String authorReportedGene, String delimiter) {

        String[] genes = authorReportedGene.split(delimiter);
        Collection<String> genesToCreate = new ArrayList<>();

        for (String gene : genes) {
            String trimmedGene = gene.trim();
            genesToCreate.add(trimmedGene);
        }

        return createGene(genesToCreate);
    }

    public Collection<Gene> createGene(Collection<String> authorReportedGenes) {
        Collection<Gene> locusGenes = new ArrayList<Gene>();
        for (String authorReportedGene : authorReportedGenes) {
            authorReportedGene = StringProcessingService.tidy_curator_entered_string(authorReportedGene);
            Gene gene = new Gene();
            gene.setGeneName(authorReportedGene);
            locusGenes.add(gene);
        }
        return locusGenes;
    }

    public RiskAllele createRiskAllele(String curatorEnteredRiskAllele, SingleNucleotidePolymorphism snp) {

        //Create new risk allele, at present we always create a new risk allele for each locus within an association
        RiskAllele riskAllele = new RiskAllele();
        riskAllele.setRiskAlleleName(StringProcessingService.tidy_curator_entered_string(curatorEnteredRiskAllele));
        riskAllele.setSnp(snp);
        return riskAllele;
    }

    public SingleNucleotidePolymorphism createSnp(String curatorEnteredSNP) {
        curatorEnteredSNP = StringProcessingService.tidy_curator_entered_string(curatorEnteredSNP);
        SingleNucleotidePolymorphism snp = new SingleNucleotidePolymorphism();
        snp.setRsId(curatorEnteredSNP);
        return snp;
    }

    public Collection<EfoTrait> getEfoTraitsFromRepository(Collection<String> efoUris) {
        Collection<EfoTrait> efoTraits = new ArrayList<>();
        for (String uri : efoUris) {
            String fullUri;
            if (uri.contains("EFO")) {
                fullUri = "http://www.ebi.ac.uk/efo/".concat(uri);
            }
            else if (uri.contains("Orphanet")) {
                fullUri = "http://www.orpha.net/ORDO/".concat(uri);
            }
            else {
                fullUri = "http://purl.obolibrary.org/obo/".concat(uri);
            }

            Collection<EfoTrait> traits = efoTraitRepository.findByUri(fullUri);

            for (EfoTrait trait : traits) {
                efoTraits.add(trait);
            }
        }
        return efoTraits;
    }
}