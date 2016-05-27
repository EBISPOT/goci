package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.GenomicContext;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;

import java.util.Collection;
import java.util.Date;

/**
 * Created by emma on 08/04/2016.
 *
 * @author emma
 *         <p>
 *         SingleNucleotidePolymorphism builder for use during testing
 */
public class SingleNucleotidePolymorphismBuilder {

    private SingleNucleotidePolymorphism singleNucleotidePolymorphism = new SingleNucleotidePolymorphism();

    public SingleNucleotidePolymorphismBuilder setId(Long id) {
        singleNucleotidePolymorphism.setId(id);
        return this;
    }

    public SingleNucleotidePolymorphismBuilder setRsId(String rsId) {
        singleNucleotidePolymorphism.setRsId(rsId);
        return this;
    }

    public SingleNucleotidePolymorphismBuilder setMerged(Long merged) {
        singleNucleotidePolymorphism.setMerged(merged);
        return this;
    }

    public SingleNucleotidePolymorphismBuilder setFunctionalClass(String functionalClass) {
        singleNucleotidePolymorphism.setFunctionalClass(functionalClass);
        return this;
    }

    public SingleNucleotidePolymorphismBuilder setLastUpdateDate(Date lastUpdateDate) {
        singleNucleotidePolymorphism.setLastUpdateDate(lastUpdateDate);
        return this;
    }

    public SingleNucleotidePolymorphismBuilder setLocations(Collection<Location> locations) {
        singleNucleotidePolymorphism.setLocations(locations);
        return this;
    }

    public SingleNucleotidePolymorphismBuilder setGenomicContexts(Collection<GenomicContext> genomicContexts) {
        singleNucleotidePolymorphism.setGenomicContexts(genomicContexts);
        return this;
    }

    public SingleNucleotidePolymorphismBuilder setRiskAlleles(Collection<RiskAllele> riskAlleles) {
        singleNucleotidePolymorphism.setRiskAlleles(riskAlleles);
        return this;
    }

    public SingleNucleotidePolymorphism build() {
        return singleNucleotidePolymorphism;
    }
}