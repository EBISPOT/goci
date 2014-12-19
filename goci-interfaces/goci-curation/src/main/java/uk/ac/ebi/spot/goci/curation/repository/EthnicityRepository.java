package uk.ac.ebi.spot.goci.curation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.spot.goci.curation.model.Ethnicity;

import java.util.Collection;

/**
 * Created by emma on 28/11/14.
 * @author emma
 *
 * Repository accessing Ethnicity entity object
 */
@RepositoryRestResource
public interface EthnicityRepository extends JpaRepository<Ethnicity, Long> {

    Collection<Ethnicity> findByStudyID(String studyID);

    Collection<Ethnicity> findByStudyIDAndType(String studyID, String Type);


}
