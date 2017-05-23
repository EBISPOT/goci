package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.WeeklyTracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Created by Cinzia on 9/11/16.
 *
 * @author Cinzia
 *           Native queries to help to generate the report
 */
@RepositoryRestResource(exported = false)
public interface WeeklyTrackingRepository extends JpaRepository<WeeklyTracking, Long> {


    @Query(value =
            "select week, year, sum(creation_study), sum(published_study), " +
            "sum(level1done_study), sum(level2done_study), " +
            "sum(creation_pubmed), sum(published_pubmed), " +
            "sum(level1done_pubmed), sum(level2done_pubmed) " +
            "from ( " +
            "        select week, year, MAX(DECODE(status, 'Creation Study', tot_study_id, 0 )) creation_study, " +
            "        MAX(DECODE(status, 'Publication Study', tot_study_id, 0 )) published_study, " +
            "       MAX(DECODE(status, 'Creation Study', tot_pubmed_id, 0 )) creation_pubmed, " +
            "        MAX(DECODE(status, 'Publication Study', tot_pubmed_id, 0 )) published_pubmed, " +
            "        0 level1done_study,0 level2done_study,0 level1done_pubmed, 0 level2done_pubmed " +
            "        from (select week, year, status, count(pubmed_id) as tot_pubmed_id, sum(study) tot_study_id from " +
            "                        (SELECT wt.year,wt.week, wt.status, count(wt.study_id) as study, wt.pubmed_id from weekly_tracking wt group by wt.week, wt.year, wt.pubmed_id, wt.status) " +
            "                group by week, year, status) " +
            "        group by week, year " +
            "        union " +
            "        select week, year, 0 creation_study, 0 published_study,0 creation_pubmed, 0 published_pubmed, " +
            "        MAX(DECODE(level_curation, 'Level_1', tot_study_id, 0 )) level1done_study, " +
            "        MAX(DECODE(level_curation, 'Level_2', tot_study_id, 0 )) level2done_study, " +
            "        MAX(DECODE(level_curation, 'Level_1', tot_pubmed_id, 0 )) level1done_pubmed, " +
            "        MAX(DECODE(level_curation, 'Level_2', tot_pubmed_id, 0 )) level2done_pubmed " +
            "        from ( " +
            "                select week, year, level_curation, count(pubmed_id) as tot_pubmed_id, sum(study) tot_study_id from " +
            "                        (select week, year, level_curation, count(study_id) as study, pubmed_id from curator_tracking " +
            "                                group by week, year, level_curation,pubmed_id) " +
            "                group by week, year, level_curation) " +
            "        group by week, year) " +
            "group by week, year " +
            "order by year desc, week desc "
            ,nativeQuery = true)
    List<Object> findAllWeekStatsReport();



    @Query(value = "select week, year," +
            "MAX(DECODE(status, 'Creation Study', tot_study_id, 0 )) creation_study," +
            "MAX(DECODE(status, 'Publication Study', tot_study_id, 0 )) Published_study," +
            "MAX(DECODE(status, 'In level 1 queue', tot_study_id, 0 )) Level_1_study," +
            "MAX(DECODE(status, 'In level 2 queue', tot_study_id, 0 )) Level_2_study," +
            "MAX(DECODE(status, 'In level 3 queue', tot_study_id, 0 )) Level_3_study, " +
            "MAX(DECODE(status, 'Creation Study', tot_pubmed_id, 0 )) creation_pubmed," +
            "MAX(DECODE(status, 'Publication Study', tot_pubmed_id, 0 )) Published_pubmed," +
            "MAX(DECODE(status, 'In level 1 queue', tot_pubmed_id, 0 )) Level_1_pubmed," +
            "MAX(DECODE(status, 'In level 2 queue', tot_pubmed_id, 0 )) Level_2_pubmed," +
            "MAX(DECODE(status, 'In level 3 queue', tot_pubmed_id, 0 )) Level_3_pubmed " +
            "from (select week, year, status, count(pubmed_id) as tot_pubmed_id, sum(study) tot_study_id from " +
            "(SELECT wt.year,wt.week, wt.status, count(wt.study_id) as study, wt.pubmed_id from weekly_tracking wt group by wt.week, wt.year, wt.pubmed_id, wt.status) " +
            "group by week, year, status) " +
            "group by week, year " +
            "order by year desc, week desc",
            nativeQuery = true)
    List<Object> findAllWeekStatsByStatus();


    @Query(value = "select  res.year, res.week from  (select wt.year, wt.week from weekly_tracking wt  where status like '%queue%' order by year asc , week asc ) res where ROWNUM <= 1",
           nativeQuery = true)
    ArrayList<Object[]> getMinYearWeek();

    List<WeeklyTracking> findByStatusAndYearAndWeek(String status, Integer year, Integer week);

    @Query(value = "select wt.study_id from weekly_tracking wt where status = 'Creation Study' and year < 2016 " +
            " minus select wt.study_id from weekly_tracking wt where year < 2016 " +
            " and status = 'Publication Study'",
            nativeQuery = true
    )
    HashSet<Long> findBase();


    @Query(value = " select we.* from weekly_tracking we where study_id in " +
            " (select wt.study_id from weekly_tracking wt where status = 'In level 1 queue' and year <= 2015 " +
            " minus " +
            " select distinct wt.study_id from weekly_tracking wt where status in ('In level 2 queue','In level 3 queue','Publication Study') and year <= 2015) " +
            " and status = 'In level 1 queue' ",
            nativeQuery = true
    )
    List<WeeklyTracking> find2016QueueLevel1();


    @Query(value = " select we.* from weekly_tracking we where study_id in " +
            " (select wt.study_id from weekly_tracking wt where status = 'In level 2 queue' and year <= 2015 " +
            " minus " +
            " select distinct wt.study_id from weekly_tracking wt where status in ('In level 3 queue','Publication Study') and year <= 2015) " +
            " and status = 'In level 2 queue' ",
            nativeQuery = true
    )
    List<WeeklyTracking> find2016QueueLevel2();


    @Query(value = "select wt.study_id from weekly_tracking wt where wt.status = :status and week = :week and year = :year", nativeQuery = true)
    HashSet<Long> findStudyByStatusAndYearAndWeek(@Param("status") String status, @Param("year") Integer year, @Param("week") Integer week);

    List<WeeklyTracking> findByStudy(Study study);
}
