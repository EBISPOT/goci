package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.EfoTrait;

/**
 * Created by emma on 04/12/14.
 * @author emma
 *
 * Repository accessing EfoTrait entity object
 */

@RepositoryRestResource
public interface EfoTraitRepository extends JpaRepository<EfoTrait, Long> {
}

