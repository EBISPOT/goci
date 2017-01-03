package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Curator;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Curator entity object
 */
@RepositoryRestResource(exported = false)
public interface CuratorRepository extends JpaRepository<Curator, Long> {

    Curator findByLastName(String lastName);

    Curator findByLastNameIgnoreCase(String lastName);

    Curator findByEmail(String email);
}
