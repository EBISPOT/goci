package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Region;

/**
 * Created by Dani on 16/11/16.
 */

@RepositoryRestResource(collectionResourceRel = "regions", path = "regions")
public interface ReadOnlyRegionRepository extends ReadOnlyRepository<Region, Long> {
}
