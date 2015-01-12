package uk.ac.ebi.spot.goci.ui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.ui.model.EFOTrait;

/**
 * Created by emma on 04/12/14.
 * @author emma
 *
 * Repository accessing EFOTrait entity object
 */

@RepositoryRestResource
public interface EFOTraitRepository extends JpaRepository<EFOTrait, Long> {
}

