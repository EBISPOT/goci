package uk.ac.ebi.spot.goci.ui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.ui.model.Association;
import java.util.Collection;


/**
 * Created by emma on 26/11/14.
 * @author emma
 *
 * Repository accessing Association entity object
 */
@RepositoryRestResource
public interface AssociationRepository extends JpaRepository<Association, Long> {

    Collection<Association> findByStudyID(String studyID);
}
