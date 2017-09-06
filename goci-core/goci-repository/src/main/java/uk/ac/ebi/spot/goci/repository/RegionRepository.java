package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Region;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Region entity object
 */

@RepositoryRestResource(exported = false)
public interface RegionRepository extends JpaRepository<Region, Long> {

    Region findByName(String regionName);

}
