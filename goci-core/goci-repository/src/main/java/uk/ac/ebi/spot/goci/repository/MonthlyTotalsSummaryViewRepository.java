package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.MonthlyTotalsSummaryView;

/**
 * Created by emma on 22/09/2015.
 *
 * @author emma
 *         <p>
 *         Repository accessing montly curator totals view
 */
@RepositoryRestResource
public interface MonthlyTotalsSummaryViewRepository extends JpaRepository<MonthlyTotalsSummaryView, Long> {


}
