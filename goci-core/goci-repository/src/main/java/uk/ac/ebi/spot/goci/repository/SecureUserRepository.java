package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.SecureUser;

/**
 * Created by emma on 09/02/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing User entity object
 */
@RepositoryRestResource(exported = false)
public interface SecureUserRepository extends JpaRepository<SecureUser, Long> {
    SecureUser findByEmail(String email);
}
