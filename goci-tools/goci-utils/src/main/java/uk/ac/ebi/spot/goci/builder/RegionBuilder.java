package uk.ac.ebi.spot.goci.builder;

import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Region;

import java.util.Collection;

/**
 * Created by emma on 08/04/2016.
 *
 * @author emma
 *         <p>
 *         Region builder for use during testing
 */
public class RegionBuilder {

    private Region region = new Region();

    public RegionBuilder setId(Long id) {
        region.setId(id);
        return this;
    }

    public RegionBuilder setName(String name) {
        region.setName(name);
        return this;
    }

    public RegionBuilder setLocations(Collection<Location> locations) {
        region.setLocations(locations);
        return this;
    }

    public Region build() {
        return region;
    }
}
