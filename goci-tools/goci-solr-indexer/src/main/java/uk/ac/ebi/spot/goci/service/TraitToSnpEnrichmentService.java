package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.TraitDocument;

import java.util.Collection;

/**
 * Created by dwelter on 12/02/15.
 */
public class TraitToSnpEnrichmentService implements DocumentEnrichmentService<TraitDocument> {

    private SingleNucleotidePolymorphismService snpService;
    private GeneService geneService;
    //TO DO - ADD REPORTED GENE STUFF


    @Autowired
    public TraitToSnpEnrichmentService(SingleNucleotidePolymorphismService snpService) {
        this.snpService = snpService;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void doEnrichment(TraitDocument document) {
        Collection<SingleNucleotidePolymorphism> singleNucleotidePolymorphisms = snpService
                .deepFindByStudyId(Long.valueOf(document.getId().split(":")[1]));
        singleNucleotidePolymorphisms.forEach(snp -> {
            document.addRsId(snp.getRsId());
            document.addChromosomePosition(snp.getChromosomePosition());
            snp.getRegions().forEach(region -> document.addRegion(region.getName()));

            snp.getGenomicContexts().forEach(genomicContext -> document.addMappedGene(genomicContext.getGene()));
        });
    }


}
