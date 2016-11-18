package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Location;

/**
 * Created by Dani on 16/11/16.
 */

@RepositoryRestResource(collectionResourceRel = "locations", path = "locations")
public interface ReadOnlyLocationRepository extends ReadOnlyRepository<Location, Long>{
}
