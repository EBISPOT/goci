package uk.ac.ebi.spot.goci.curation.builder;

import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;

/**
 * Created by emma on 08/04/2016.
 *
 * @author emma Location builder for use during testing
 */
public class LocationBuilder {

    private Location location = new Location();

    public LocationBuilder setId(Long id) {
        location.setId(id);
        return this;
    }

    public LocationBuilder setChromosomeName(String chromosomeName) {
        location.setChromosomeName(chromosomeName);
        return this;
    }

    public LocationBuilder setChromosomePosition(String chromosomePosition) {
        location.setChromosomePosition(chromosomePosition);
        return this;
    }

    public LocationBuilder setRegion(Region region) {
        location.setRegion(region);
        return this;
    }

    public Location build() {
        return location;
    }
}
