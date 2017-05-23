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
@RepositoryRestResource(exported = false)
public interface MonthlyTotalsSummaryViewRepository extends JpaRepository<MonthlyTotalsSummaryView, Long> {

    @Query("select distinct month from MonthlyTotalsSummaryView order by month desc") List<Integer> getAllMonths();

    @Query("select distinct year from MonthlyTotalsSummaryView order by year desc") List<Integer> getAllYears();

    List<MonthlyTotalsSummaryView> findByYearOrderByYearDesc(Integer year);

    List<MonthlyTotalsSummaryView> findByYearAndMonthOrderByYearDesc(Integer year, Integer month);

    List<MonthlyTotalsSummaryView> findByMonthOrderByYearDesc(Integer month);

    List<MonthlyTotalsSummaryView> findByCurator(String curator);

    List<MonthlyTotalsSummaryView> findByCurationStatus(String curationStatus);

    List<MonthlyTotalsSummaryView> findByCuratorAndCurationStatus(String curator, String curationStatus);

    List<MonthlyTotalsSummaryView> findByCuratorAndCurationStatusAndYearAndMonthOrderByYearDesc(String curator,
                                                                                                String curationStatus,
                                                                                                Integer year,
                                                                                                Integer month);

    List<MonthlyTotalsSummaryView> findByCurationStatusAndYearAndMonthOrderByYearDesc(String statusName,
                                                                                      Integer year,
                                                                                      Integer month);

    List<MonthlyTotalsSummaryView> findByCuratorAndCurationStatusAndYearOrderByYearDesc(String curatorName,
                                                                                        String statusName,
                                                                                        Integer year);

    List<MonthlyTotalsSummaryView> findByCuratorAndCurationStatusAndMonthOrderByYearDesc(String curatorName,
                                                                                         String statusName,
                                                                                         Integer month);

    List<MonthlyTotalsSummaryView> findByCuratorAndYearAndMonthOrderByYearDesc(String curatorName,
                                                                               Integer year,
                                                                               Integer month);

    List<MonthlyTotalsSummaryView> findByCuratorAndYearOrderByYearDesc(String curatorName, Integer year);

    List<MonthlyTotalsSummaryView> findByCuratorAndMonthOrderByYearDesc(String curatorName, Integer month);

    List<MonthlyTotalsSummaryView> findByCurationStatusAndYearOrderByYearDesc(String statusName, Integer year);

    List<MonthlyTotalsSummaryView> findByCurationStatusAndMonthOrderByYearDesc(String statusName, Integer month);
}
