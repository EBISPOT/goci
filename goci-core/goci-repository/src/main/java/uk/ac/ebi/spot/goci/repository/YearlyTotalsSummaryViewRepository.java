package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.YearlyTotalsSummaryView;

import java.util.List;

/**
 * Created by emma on 22/09/2015.
 *
 * @author emma
 *         <p>
 *         Repository accessing yearly curator totals view
 */
@RepositoryRestResource(exported = false)
public interface YearlyTotalsSummaryViewRepository extends JpaRepository<YearlyTotalsSummaryView, Long> {
    @Query("select distinct year from YearlyTotalsSummaryView order by year desc") List<Integer> getAllYears();

    List<YearlyTotalsSummaryView> findByCuratorAndCurationStatusAndYearOrderByYearDesc(String curatorName,
                                                                                       String statusName,
                                                                                       Integer year);

    List<YearlyTotalsSummaryView> findByCuratorAndCurationStatus(String curatorName, String statusName);

    List<YearlyTotalsSummaryView> findByCurationStatusAndYearOrderByYearDesc(String statusName, Integer year);

    List<YearlyTotalsSummaryView> findByCurationStatus(String statusName);

    List<YearlyTotalsSummaryView> findByCuratorAndYearOrderByYearDesc(String curatorName, Integer year);

    List<YearlyTotalsSummaryView> findByCurator(String curatorName);

    List<YearlyTotalsSummaryView> findByYearOrderByYearDesc(Integer year);
}
