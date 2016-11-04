package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.DeletedStudy;

/**
 * Created by emma on 31/05/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Deleted Study entity object
 */
@RepositoryRestResource(exported = false)
public interface DeletedStudyRepository extends JpaRepository<DeletedStudy, Long> {
}

