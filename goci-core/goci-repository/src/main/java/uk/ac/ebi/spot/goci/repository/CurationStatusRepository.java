package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.CurationStatus;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Repository to access CurationStatus entity object
 */
@RepositoryRestResource(exported = false)
public interface CurationStatusRepository extends JpaRepository<CurationStatus, Long> {

    CurationStatus findByStatus(String status);
}
