package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.repository.GeneRepository;
import uk.ac.ebi.spot.goci.repository.GenomicContextRepository;

/**
 * Created by emma on 02/02/2016.
 *
 * @author emma
 *         <p>
 *         Service that creates a new genomic context
 */
@Service
public class GenomicContextCreationService {

    private GeneRepository geneRepository;
    private GenomicContextRepository genomicContextRepository;

    @Autowired
    public GenomicContextCreationService(GeneRepository geneRepository,
                                         GenomicContextRepository genomicContextRepository) {
        this.geneRepository = geneRepository;
        this.genomicContextRepository = genomicContextRepository;
    }

    /**
     * Method to create genomic context
     *
     * @param isIntergenic
     * @param isUpstream
     * @param isDownstream
     * @param distance
     * @param source
     * @param mappingMethod
     * @param geneName
     * @param snpIdInDatabase
     * @param isClosestGene
     * @param location
     */
    public GenomicContext createGenomicContext(Boolean isIntergenic,
                                               Boolean isUpstream,
                                               Boolean isDownstream,
                                               Long distance,
                                               String source,
                                               String mappingMethod,
                                               String geneName,
                                               SingleNucleotidePolymorphism snpIdInDatabase,
                                               Boolean isClosestGene, Location location) {

        GenomicContext genomicContext = new GenomicContext();

        // Find gene, ignoreCase query is not used here as we want to
        // only create a genomic context for
        // the exact gene name returned from mapping
        Gene gene = geneRepository.findByGeneName(geneName);

        genomicContext.setGene(gene);
        genomicContext.setIsIntergenic(isIntergenic);
        genomicContext.setIsDownstream(isDownstream);
        genomicContext.setIsUpstream(isUpstream);
        genomicContext.setDistance(distance);
        genomicContext.setSource(source);
        genomicContext.setMappingMethod(mappingMethod);
        genomicContext.setSnp(snpIdInDatabase);
        genomicContext.setIsClosestGene(isClosestGene);
        genomicContext.setLocation(location);

        // Save genomic context
        genomicContextRepository.save(genomicContext);

        return genomicContext;
    }

}
