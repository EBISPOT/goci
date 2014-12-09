package uk.ac.ebi.spot.goci.curation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.curation.model.Housekeeping;
import uk.ac.ebi.spot.goci.curation.model.Study;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p/>
 *         Repository accessing Housekeeping entity object
 */

@RepositoryRestResource
public interface HousekeepingRepository extends JpaRepository<Housekeeping, Long> {

    Housekeeping findByStudyId(String studyID);
}
