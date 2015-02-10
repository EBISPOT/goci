package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.User;

/**
 * Created by emma on 09/02/15.
 * @author emma
 *
 * Repository accessing User entity object
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
}
