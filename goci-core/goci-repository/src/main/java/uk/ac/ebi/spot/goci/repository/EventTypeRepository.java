package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Event;
import uk.ac.ebi.spot.goci.model.EventType;

/**
 * Created by Cinzia on 11/11/2016.
 *
 * @author Cinzia
 *         <p>
 *         Repository accessing EventType entity object
 */

@RepositoryRestResource
public interface EventTypeRepository extends JpaRepository<EventType, Long> {

    EventType findByAction(String action);
    EventType findByEventType(String eventType);

}
