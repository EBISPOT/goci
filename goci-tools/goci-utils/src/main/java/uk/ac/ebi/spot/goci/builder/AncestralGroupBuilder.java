package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.AncestralGroup;
import uk.ac.ebi.spot.goci.model.Ancestry;

import java.util.Collection;

/**
 * Created by dwelter on 26/04/17.
 */
public class AncestralGroupBuilder {

    private AncestralGroup ancestralGroup = new AncestralGroup();

    public AncestralGroupBuilder setId(Long id) {
        ancestralGroup.setId(id);
        return this;
    }

    public AncestralGroupBuilder setAncestralGroup(String ancestralGr){
        ancestralGroup.setAncestralGroup(ancestralGr);
        return this;
    }

    public AncestralGroupBuilder setAncestries(Collection<Ancestry> ancestries){
        ancestralGroup.setAncestries(ancestries);
        return this;
    }

    public AncestralGroup build(){
        return ancestralGroup;
    }
}
