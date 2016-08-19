package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Platform;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;

/**
 * Created by dwelter on 17/03/16.
 *
 * Platform builder used in testing
 */
public class PlatformBuilder {

    private Platform platform;

    public PlatformBuilder setId(Long id){
        platform.setId(id);
        return  this;
    }

    public PlatformBuilder setManufacturer(String manufacturer){
        platform.setManufacturer(manufacturer);
        return this;
    }

    public PlatformBuilder setStudies(Collection<Study> studies){
        platform.setStudies(studies);
        return this;
    }

    public Platform build(){
        return platform;
    }

}
