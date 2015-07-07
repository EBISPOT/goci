package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Location;

import java.util.Collection;

/**
 * Created by Laurent on 18/05/15.
 *
 * * @author lgil
 *         <p/>
 *         Repository accessing Location entity object
 */
@RepositoryRestResource
public interface LocationRepository extends JpaRepository<Location, Long> {

  Location findByChromosomeNameAndChromosomePositionAndRegionName(String chromosomeName, String chromosomePosition, String regionName);
}
