package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.WeeklyProgressView;

import java.util.List;
import java.util.Date;

/**
 * Created by emma on 08/06/2016.
 *
 * @author emma
 *         <p>
 *         Repository accessing weekly curator progress
 */
@RepositoryRestResource(exported = false)
public interface WeeklyProgressViewRepository extends JpaRepository<WeeklyProgressView, Long> {
    @Query("SELECT DISTINCT weekStartDay FROM WeeklyProgressView ORDER BY weekStartDay ASC") List<Date> getAllWeekStartDates();
}
