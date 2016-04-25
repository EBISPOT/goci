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
////    group by trunc(study_added_date, 'D')
//
//
//    select trunc(h.study_added_date, 'D'), count(*) as entries, count(distinct s.pubmed_id) as studies
//    from housekeeping h
//    join study s on s.housekeeping_id = h.id
//    where h.study_added_date is not null
//    group by trunc(h.study_added_date, 'D')
//    order by trunc(h.study_added_date, 'D') desc



}
