package uk.ac.ebi.spot.goci.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.model.Ethnicity;

import java.util.Collection;

/**
 * Created by emma on 28/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Ethnicity entity object
 */
@RepositoryRestResource
public interface EthnicityRepository extends JpaRepository<Ethnicity, Long> {
    Collection<Ethnicity> findByStudyIdAndType(Long studyId, String Type);

    Collection<Ethnicity> findByStudyId(Long studyId);

}
