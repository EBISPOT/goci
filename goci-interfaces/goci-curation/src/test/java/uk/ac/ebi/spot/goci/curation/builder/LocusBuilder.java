package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;

import java.util.Collection;

/**
 * Created by emma on 07/04/2016.
 *
 * @author emma
 *         <p>
 *         Builder for a locus object
 */
public class LocusBuilder {

    private Locus locus = new Locus();

    public LocusBuilder setId(Long id) {
        locus.setId(id);
        return this;
    }

    public LocusBuilder setHaplotypeSnpCount(Integer haplotypeSnpCount) {
        locus.setHaplotypeSnpCount(haplotypeSnpCount);
        return this;
    }

    public LocusBuilder setDescription(String description) {
        locus.setDescription(description);
        return this;
    }

    public LocusBuilder setStrongestRiskAlleles(Collection<RiskAllele> strongestRiskAlleles) {
        locus.setStrongestRiskAlleles(strongestRiskAlleles);
        return this;
    }

    public LocusBuilder setAuthorReportedGenes(Collection<Gene> authorReportedGenes) {
        locus.setAuthorReportedGenes(authorReportedGenes);
        return this;
    }

    public Locus build() {
        return locus;
    }
}