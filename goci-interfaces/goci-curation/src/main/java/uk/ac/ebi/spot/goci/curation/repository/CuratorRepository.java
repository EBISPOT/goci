package uk.ac.ebi.spot.goci.curation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.curation.model.Curator;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p/>
 *         Repository accessing Curator entity object
 */
@RepositoryRestResource
public interface CuratorRepository extends JpaRepository<Curator, Long> {
}
