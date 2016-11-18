package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Ethnicity;

/**
 * Created by Dani on 16/11/16.
 */
@RepositoryRestResource(collectionResourceRel = "ethincities", path = "ethnicities")
public interface ReadOnlyEthnicityRepository extends ReadOnlyRepository<Ethnicity, Long> {
}
