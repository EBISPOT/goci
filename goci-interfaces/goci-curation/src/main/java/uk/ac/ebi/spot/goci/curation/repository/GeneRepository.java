package uk.ac.ebi.spot.goci.curation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.curation.model.Gene;

/**
 * Created by emma on 01/12/14.
 * @author emma
 *
 * Repository accessing Gene entity object
 */
@RepositoryRestResource
public interface GeneRepository extends JpaRepository<Gene, Long> {
}
