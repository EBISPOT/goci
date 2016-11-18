package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Platform;

/**
 * Created by Dani on 16/11/16.
 */

@RepositoryRestResource(collectionResourceRel = "platforms", path = "platforms")
public interface ReadOnlyPlatformRepository extends ReadOnlyRepository<Platform, Long>{
}
