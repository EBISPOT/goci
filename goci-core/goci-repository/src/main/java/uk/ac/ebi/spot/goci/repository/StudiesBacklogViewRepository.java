package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.StudiesBacklogView;

/**
 * Created by cinzia on 31/10/2016.
 */

@RepositoryRestResource
public interface StudiesBacklogViewRepository extends JpaRepository<StudiesBacklogView, Long> {

}
