package uk.ac.ebi.spot.goci.curation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.curation.model.Region;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p/>
 *         Repository accessing Region entity object
 */

@RepositoryRestResource
public interface RegionRepository extends JpaRepository<Region, Long> {
}
