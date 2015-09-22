package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.YearlyCuratorTotalsSummaryView;

/**
 * Created by emma on 22/09/2015.
 *
 * @author emma
 *         <p>
 *         Repository accessing yearly curator totals view
 */
@RepositoryRestResource
public interface YearlyCuratorTotalsSummaryViewRepository extends JpaRepository<YearlyCuratorTotalsSummaryView, Long> {
}
