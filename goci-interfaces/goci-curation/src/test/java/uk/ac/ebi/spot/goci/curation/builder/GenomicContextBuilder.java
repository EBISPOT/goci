package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

/**
 * Created by emma on 08/04/2016.
 *
 * @author emma
 *         <p>
 *         Builder for GenomicContext used during testing
 */
public class GenomicContextBuilder {

    private GenomicContext genomicContext = new GenomicContext();

    public GenomicContextBuilder setId(Long id) {
        genomicContext.setId(id);
        return this;
    }

    public GenomicContextBuilder setIsIntergenic(Boolean isIntergenic) {
        genomicContext.setIsIntergenic(isIntergenic);
        return this;
    }

    public GenomicContextBuilder setIsUpstream(Boolean isUpstream) {
        genomicContext.setIsUpstream(isUpstream);
        return this;
    }

    public GenomicContextBuilder setIsDownstream(Boolean isDownstream) {
        genomicContext.setIsDownstream(isDownstream);
        return this;
    }

    public GenomicContextBuilder setDistance(Long distance) {
        genomicContext.setDistance(distance);
        return this;
    }

    public GenomicContextBuilder setSnp(SingleNucleotidePolymorphism snp) {
        genomicContext.setSnp(snp);
        return this;
    }

    public GenomicContextBuilder setGene(Gene gene) {
        genomicContext.setGene(gene);
        return this;
    }

    public GenomicContextBuilder setMappingMethod(String mappingMethod) {
        genomicContext.setMappingMethod(mappingMethod);
        return this;
    }

    public GenomicContextBuilder setSource(String source) {
        genomicContext.setSource(source);
        return this;
    }

    public GenomicContextBuilder setIsClosestGene(Boolean isClosestGene) {
        genomicContext.setIsClosestGene(isClosestGene);
        return this;
    }

    public GenomicContextBuilder setLocation(Location location) {
        genomicContext.setLocation(location);
        return this;
    }

    public GenomicContext build() {
        return genomicContext;
    }
}
