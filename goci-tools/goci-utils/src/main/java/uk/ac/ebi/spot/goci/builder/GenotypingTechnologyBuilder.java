package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.GenotypingTechnology;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;

/**
 * Created by dwelter on 22/06/17.
 */
public class GenotypingTechnologyBuilder {
    private GenotypingTechnology genotypingTechnology = new GenotypingTechnology();

    public GenotypingTechnologyBuilder setId(Long id) {
        genotypingTechnology.setId(id);
        return this;
    }

    public GenotypingTechnologyBuilder setGenotypingTechnology(String ancestralGr){
        genotypingTechnology.setGenotypingTechnology(ancestralGr);
        return this;
    }

    public GenotypingTechnologyBuilder setStudies(Collection<Study> studies){
        genotypingTechnology.setStudies(studies);
        return this;
    }
    public GenotypingTechnology build(){
        return genotypingTechnology;
    }
}
