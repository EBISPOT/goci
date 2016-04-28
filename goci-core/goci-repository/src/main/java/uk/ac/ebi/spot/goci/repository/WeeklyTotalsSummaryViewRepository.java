package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.WeeklyTotalsSummaryView;

import java.util.List;

/**
 * Created by dwelter on 15/04/16.
 */
@RepositoryRestResource
public interface WeeklyTotalsSummaryViewRepository extends JpaRepository<WeeklyTotalsSummaryView, Long> {

    @Query("select week, weekly_studies, weekly_entries from WeeklyTotalsSummaryView where rownum < 9") List<WeeklyTotalsSummaryView> getLastEightWeeks();



}
