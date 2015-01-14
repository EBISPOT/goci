package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

/**
 * Created by emma on 01/12/14.
 * @author emma
 *
 * Repository accessing DiseaseTrait entity object
 */

@RepositoryRestResource
public interface DiseaseTraitRepository extends JpaRepository<DiseaseTrait, Long>{

    String findByTraitIgnoreCase(String trait);
}
