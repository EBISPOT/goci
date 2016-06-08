package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.EventType;
import uk.ac.ebi.spot.goci.model.WeeklyProgressView;

import java.util.List;

/**
 * Created by emma on 08/06/2016.
 *
 * @author emma
 *         <p>
 *         Repository accessing weekly curator progress
 */
@RepositoryRestResource
public interface WeeklyProgressViewRepository extends JpaRepository<WeeklyProgressView, Long> {

    List<WeeklyProgressView> findByEventType(EventType eventType);
}
