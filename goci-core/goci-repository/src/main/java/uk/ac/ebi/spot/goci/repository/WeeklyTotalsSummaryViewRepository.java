package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.WeeklyTotalsSummaryView;

import java.util.List;

/**
 * Created by dwelter on 15/04/16.
 */
@RepositoryRestResource(exported = false)
public interface WeeklyTotalsSummaryViewRepository extends JpaRepository<WeeklyTotalsSummaryView, Long> {

   @Query("select w from WeeklyTotalsSummaryView w where rownum < 9") List<WeeklyTotalsSummaryView> getLastEightWeeks();



}
