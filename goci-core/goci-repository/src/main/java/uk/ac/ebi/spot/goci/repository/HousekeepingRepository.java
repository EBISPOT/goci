package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Housekeeping;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Housekeeping entity object
 */

@RepositoryRestResource(exported = false)
public interface HousekeepingRepository extends JpaRepository<Housekeeping, Long> {

}
