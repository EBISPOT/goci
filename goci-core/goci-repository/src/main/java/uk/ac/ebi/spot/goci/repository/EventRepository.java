package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Event;

import java.util.List;

/**
 * Created by emma on 06/05/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Event entity object
 */
@RepositoryRestResource(exported = false)
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserId(Long id);
}

