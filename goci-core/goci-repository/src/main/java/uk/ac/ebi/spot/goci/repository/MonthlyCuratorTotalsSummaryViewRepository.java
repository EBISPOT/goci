package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.MonthlyCuratorTotalsSummaryView;

import java.util.List;

/**
 * Created by emma on 22/09/2015.
 *
 * @author emma
 *         <p>
 *         Repository accessing montly curator totals view
 */
@RepositoryRestResource
public interface MonthlyCuratorTotalsSummaryViewRepository extends JpaRepository<MonthlyCuratorTotalsSummaryView, Long> {
    

}
