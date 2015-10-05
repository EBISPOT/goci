package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.MonthlyTotalsSummaryView;

import java.util.List;

/**
 * Created by emma on 22/09/2015.
 *
 * @author emma
 *         <p>
 *         Repository accessing montly curator totals view
 */
@RepositoryRestResource
public interface MonthlyTotalsSummaryViewRepository extends JpaRepository<MonthlyTotalsSummaryView, Long> {

    List<MonthlyTotalsSummaryView> findByYearOrderByYearDesc(Integer year);

    List<MonthlyTotalsSummaryView> findByYearAndMonthOrderByYearDesc(Integer year, Integer month);

    List<MonthlyTotalsSummaryView> findByCurator(String curator);

    List<MonthlyTotalsSummaryView> findByCurationStatus(String curationStatus);

    List<MonthlyTotalsSummaryView> findByCuratorAndCurationStatus(String curator, String curationStatus);

    @Query("select distinct month from MonthlyTotalsSummaryView order by month desc")
    List<Integer> getAllMonths();

    @Query("select distinct year from MonthlyTotalsSummaryView order by year desc")
    List<Integer> getAllYears();


}
