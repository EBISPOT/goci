package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.CuratorTracking;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.List;

/**
 * Created by Cinzia on 01/12/16.
 *
 * @author Cinzia
 *         <p>
 *         Native queries to help to generate the report.
 */

@RepositoryRestResource(exported = false)
public interface CuratorTrackingRepository extends JpaRepository<CuratorTracking, Long> {

    @Query(value = "select distinct ct.curator_name from curator_tracking ct order by ct.curator_name",
            nativeQuery = true)
    List<String> findAllCurators();

    @Query(value="SELECT curator_name, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_1', tot_study, 0 )) Level1Done, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_2', tot_study, 0 )) Level2Done, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_3', tot_study, 0 )) Level3Done, " +
            "MAX(DECODE(LEVEL_CURATION, 'Published', tot_study, 0 )) Published, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_1', tot_pubmed, 0 )) Level1DonePubmed, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_2', tot_pubmed, 0 )) Level2DonePubmed, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_3', tot_pubmed, 0 )) Level3DonePubmed, " +
            "MAX(DECODE(LEVEL_CURATION, 'Published', tot_pubmed, 0 )) PublishedPubmed " +
            "FROM (select  LEVEL_CURATION, curator_name, count(*) tot_pubmed, sum(xstudy) tot_study from ( " +
            "select week, year, pubmed_id, curator_name, LEVEL_CURATION, count(*) xstudy " +
            "from curator_tracking " +
            "where week=:week and year = :year " +
            "group by week, year, pubmed_id, curator_name, LEVEL_CURATION) " +
            "group by week, year, LEVEL_CURATION, curator_name) " +
            "group by curator_name " +
            "order by curator_name",
            nativeQuery = true)
    List<Object> statsByWeek(@Param("year") int year,@Param("week") int week);

    @Query(value="select year, week, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_1', tot_study, 0 )) Level1Done, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_2', tot_study, 0 )) Level2Done, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_3', tot_study, 0 )) Level3Done, " +
            "MAX(DECODE(LEVEL_CURATION, 'Published', tot_study, 0 )) Published, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_1', tot_pubmed, 0 )) Level1DonePubmed, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_2', tot_pubmed, 0 )) Level2DonePubmed, " +
            "MAX(DECODE(LEVEL_CURATION, 'Level_3', tot_pubmed, 0 )) Level3DonePubmed, " +
            "MAX(DECODE(LEVEL_CURATION, 'Published', tot_pubmed, 0 )) PublishedPubmed " +
            "FROM ( " +
            "select  week, year, LEVEL_CURATION,count(*) tot_pubmed, sum(xstudy) tot_study from ( " +
            "select week, year, pubmed_id, LEVEL_CURATION, count(*) xstudy " +
            "from curator_tracking " +
            "where curator_name = :curatorName " +
            "group by week, year, pubmed_id, LEVEL_CURATION) " +
            "group by week, year, LEVEL_CURATION) " +
            "group by year, week " +
            "order by year desc, week desc",
            nativeQuery = true)
    List<Object> statsByCuration(@Param("curatorName") String curatorName);

    List<CuratorTracking> findByStudy(Study study);
}

