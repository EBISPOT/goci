package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;

/**
 * Created by emma on 15/12/2015.
 *
 * @author emma
 *         <p>
 *         Disease trait builder used in testing
 */
public class DiseaseTraitBuilder {

    private DiseaseTrait diseaseTrait = new DiseaseTrait();

    public DiseaseTraitBuilder setId(Long id) {
        diseaseTrait.setId(id);
        return this;
    }

    public DiseaseTraitBuilder setTrait(String trait) {
        diseaseTrait.setTrait(trait);
        return this;
    }

    public DiseaseTraitBuilder setStudies(Collection<Study> studies) {
        diseaseTrait.setStudies(studies);
        return this;
    }

    public DiseaseTrait build() {
        return diseaseTrait;
    }

}
