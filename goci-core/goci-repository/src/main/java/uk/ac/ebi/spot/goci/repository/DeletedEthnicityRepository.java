package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.DeletedEthnicity;

/**
 * Created by emma on 05/08/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Deleted Ethnicity entity object
 */
@RepositoryRestResource
public interface DeletedEthnicityRepository extends JpaRepository<DeletedEthnicity, Long> {
}

