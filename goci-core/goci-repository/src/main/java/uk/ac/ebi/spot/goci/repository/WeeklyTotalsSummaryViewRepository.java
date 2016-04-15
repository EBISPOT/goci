package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.WeeklyTotalsSummaryView;

/**
 * Created by dwelter on 15/04/16.
 */
@RepositoryRestResource
public interface WeeklyTotalsSummaryViewRepository extends JpaRepository<WeeklyTotalsSummaryView, Long> {

    //query to get studies grouped by start day of the week
//    select trunc(study_added_date, 'D'), count(*) from housekeeping
//    group by trunc(study_added_date, 'D')

}
