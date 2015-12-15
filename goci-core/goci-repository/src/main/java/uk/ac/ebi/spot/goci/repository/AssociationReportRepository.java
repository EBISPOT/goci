package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.AssociationReport;

/**
 * Created by emma on 11/02/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing Association Report entity object
 */
@RepositoryRestResource
public interface AssociationReportRepository extends JpaRepository<AssociationReport, Long> {

    AssociationReport findByAssociationId(Long id);
}
