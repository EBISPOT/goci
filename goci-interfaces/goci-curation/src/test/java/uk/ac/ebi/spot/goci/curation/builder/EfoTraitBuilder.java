package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;

/**
 * Created by emma on 26/02/2016.
 *
 * @author emma
 *         <p>
 *         EFO trait builder used in testing
 */
public class EfoTraitBuilder {

    private EfoTrait efoTrait;

    public EfoTraitBuilder setId(Long id) {
        efoTrait.setId(id);
        return this;
    }

    public EfoTraitBuilder setTrait(String trait) {
        efoTrait.setTrait(trait);
        return this;
    }

    public EfoTraitBuilder setUri(String uri) {
        efoTrait.setUri(uri);
        return this;
    }

    public EfoTraitBuilder setStudies(Collection<Study> studies) {
        efoTrait.setStudies(studies);
        return this;
    }

    public EfoTraitBuilder setAssociations(Collection<Association> associations) {
        efoTrait.setAssociations(associations);
        return this;
    }

    public EfoTrait build() {
        return efoTrait;
    }
}
